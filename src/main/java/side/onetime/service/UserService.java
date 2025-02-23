package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.RefreshToken;
import side.onetime.domain.User;
import side.onetime.dto.user.request.OnboardUserRequest;
import side.onetime.dto.user.request.UpdateUserPolicyAgreementRequest;
import side.onetime.dto.user.request.UpdateUserProfileRequest;
import side.onetime.dto.user.request.UpdateUserSleepTimeRequest;
import side.onetime.dto.user.response.GetUserPolicyAgreementResponse;
import side.onetime.dto.user.response.GetUserProfileResponse;
import side.onetime.dto.user.response.GetUserSleepTimeResponse;
import side.onetime.dto.user.response.OnboardUserResponse;
import side.onetime.repository.RefreshTokenRepository;
import side.onetime.repository.UserRepository;
import side.onetime.util.JwtUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME; // 액세스 토큰 유효기간

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME; // 리프레쉬 토큰 유효기간

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * 유저 온보딩 메서드.
     *
     * 회원가입 이후, 유저의 필수 정보를 설정하고 온보딩을 완료합니다.
     * 제공된 레지스터 토큰을 검증하여 유저 정보를 확인한 후, 닉네임, 약관 동의 여부, 수면 시간을 저장합니다.
     * 저장된 유저 정보를 기반으로 새로운 액세스 토큰과 리프레쉬 토큰을 생성하고 반환합니다.
     *
     * @param onboardUserRequest 유저의 레지스터 토큰, 닉네임, 약관 동의 여부, 수면 시간 정보를 포함하는 요청 객체
     * @return 발급된 액세스 토큰과 리프레쉬 토큰을 포함하는 응답 객체
     */
    @Transactional
    public OnboardUserResponse onboardUser(OnboardUserRequest onboardUserRequest) {
        // 레지스터 토큰을 이용하여 사용자 정보 추출
        String registerToken = onboardUserRequest.registerToken();
        jwtUtil.validateToken(registerToken);

        String provider = jwtUtil.getClaimFromToken(registerToken, "provider", String.class);
        String providerId = jwtUtil.getClaimFromToken(registerToken, "providerId", String.class);
        String name = jwtUtil.getClaimFromToken(registerToken, "name", String.class);
        String email = jwtUtil.getClaimFromToken(registerToken, "email", String.class);

        User newUser = User.builder()
                .name(name)
                .email(email)
                .nickname(onboardUserRequest.nickname())
                .provider(provider)
                .providerId(providerId)
                .servicePolicyAgreement(onboardUserRequest.servicePolicyAgreement())
                .privacyPolicyAgreement(onboardUserRequest.privacyPolicyAgreement())
                .marketingPolicyAgreement(onboardUserRequest.marketingPolicyAgreement())
                .sleepStartTime(onboardUserRequest.sleepStartTime())
                .sleepEndTime(onboardUserRequest.sleepEndTime())
                .language(onboardUserRequest.language())
                .build();
        userRepository.save(newUser);
        Long userId = newUser.getId();

        // 액세스 & 리프레쉬 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(userId, ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = jwtUtil.generateRefreshToken(userId, REFRESH_TOKEN_EXPIRATION_TIME);

        // 새로운 리프레쉬 토큰 Redis 저장
        RefreshToken newRefreshToken = new RefreshToken(userId, refreshToken);
        refreshTokenRepository.save(newRefreshToken);

        // 액세스 토큰 반환
        return OnboardUserResponse.of(accessToken, refreshToken);
    }

    /**
     * 유저 정보 조회 메서드.
     *
     * 인증된 유저의 프로필 정보를 반환합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 유저 프로필 응답 데이터
     */
    @Transactional(readOnly = true)
    public GetUserProfileResponse getUserProfile(User user) {
        return GetUserProfileResponse.of(user);
    }

    /**
     * 유저 정보 수정 메서드.
     *
     * 인증된 유저의 닉네임 or 언어를 수정합니다.
     * 수정된 닉네임은 길이 제한을 검증합니다.
     *
     * @param user 인증된 사용자 정보
     * @param updateUserProfileRequest 유저 정보 수정 요청 데이터
     */
    @Transactional
    public void updateUserProfile(User user, UpdateUserProfileRequest updateUserProfileRequest) {
        Optional.ofNullable(updateUserProfileRequest.nickname()).ifPresent(user::updateNickName);
        Optional.ofNullable(updateUserProfileRequest.language()).ifPresent(user::updateLanguage);
        userRepository.save(user);
    }

    /**
     * 유저 서비스 탈퇴 메서드.
     *
     * 인증된 유저의 계정을 삭제합니다.
     *
     * @param user 인증된 사용자 정보
     */
    @Transactional
    public void withdrawService(User user) {
        userRepository.delete(user);
    }

    /**
     * 유저 약관 동의 여부 조회 메서드.
     *
     * 인증된 사용자의 필수 및 선택 약관 동의 상태를 반환합니다.
     * 값이 null일 경우 기본값(false)을 반환합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 유저 약관 동의 여부 응답 객체
     */
    @Transactional(readOnly = true)
    public GetUserPolicyAgreementResponse getUserPolicyAgreement(User user) {
        return GetUserPolicyAgreementResponse.from(user);
    }

    /**
     * 유저 약관 동의 여부 수정 메서드.
     *
     * 인증된 유저의 서비스 이용약관, 개인정보 수집 및 이용 동의, 마케팅 정보 수신 동의 상태를 업데이트합니다.
     * 모든 필드는 필수 값이며, `@NotNull` 검증을 거칩니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 약관 동의 여부 수정 요청 데이터
     */
    @Transactional
    public void updateUserPolicyAgreement(User user, UpdateUserPolicyAgreementRequest request) {
        user.updateServicePolicyAgreement(request.servicePolicyAgreement());
        user.updatePrivacyPolicyAgreement(request.privacyPolicyAgreement());
        user.updateMarketingPolicyAgreement(request.marketingPolicyAgreement());
        userRepository.save(user);
    }

    /**
     * 유저 수면 시간 조회 메서드.
     *
     * 인증된 사용자의 수면 시작 시간과 종료 시간을 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 유저 수면 시간 응답 데이터 (시작 시간 및 종료 시간 포함)
     */
    @Transactional(readOnly = true)
    public GetUserSleepTimeResponse getUserSleepTime(User user) {
        return GetUserSleepTimeResponse.from(user);
    }

    /**
     * 유저 수면 시간 수정 메서드.
     *
     * 인증된 사용자의 수면 시작 시간과 종료 시간을 업데이트합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 수면 시간 수정 요청 데이터 (필수 값)
     */
    @Transactional
    public void updateUserSleepTime(User user, UpdateUserSleepTimeRequest request) {
        user.updateSleepStartTime(request.sleepStartTime());
        user.updateSleepEndTime(request.sleepEndTime());
        userRepository.save(user);
    }
}
