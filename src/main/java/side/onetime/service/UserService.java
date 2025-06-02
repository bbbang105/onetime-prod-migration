package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.auth.dto.AuthTokenResponse;
import side.onetime.domain.RefreshToken;
import side.onetime.domain.User;
import side.onetime.dto.user.request.OnboardUserRequest;
import side.onetime.dto.user.request.UpdateUserPolicyAgreementRequest;
import side.onetime.dto.user.request.UpdateUserProfileRequest;
import side.onetime.dto.user.request.UpdateUserSleepTimeRequest;
import side.onetime.dto.user.response.GetUserPolicyAgreementResponse;
import side.onetime.dto.user.response.GetUserProfileResponse;
import side.onetime.dto.user.response.GetUserSleepTimeResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.UserErrorStatus;
import side.onetime.repository.RefreshTokenRepository;
import side.onetime.repository.UserRepository;
import side.onetime.util.JwtUtil;
import side.onetime.util.UserAuthorizationUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${jwt.access-token.expiration-time}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration-time}")
    private long refreshTokenExpiration;

    @Value("${cookie.domain}")
    private String cookieDomain;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * 유저 온보딩 처리 메서드.
     *
     * 회원가입 이후 유저 정보를 저장하고, 액세스 토큰과 리프레시 토큰을 발급합니다.
     *
     * @param request 유저의 온보딩 정보가 포함된 요청 객체
     * @param browserId User-Agent 기반으로 생성된 브라우저 식별 ID
     * @return 발급된 액세스 토큰 정보
     */
    @Transactional
    public AuthTokenResponse onboardUser(OnboardUserRequest request, String browserId) {
        User newUser = createUserFromRegisterToken(request);
        userRepository.save(newUser);

        Long userId = newUser.getId();
        String accessToken = jwtUtil.generateAccessToken(userId, "USER");
        String refreshToken = jwtUtil.generateRefreshToken(userId);
        refreshTokenRepository.save(new RefreshToken(userId, browserId, refreshToken));

        return AuthTokenResponse.of(accessToken, refreshToken, accessTokenExpiration, refreshTokenExpiration, cookieDomain);
    }

    /**
     * 레지스터 토큰을 기반으로 User 엔티티를 생성하는 메서드.
     *
     * JWT 레지스터 토큰의 유효성을 검증하고, 토큰에서 제공자 정보 및 유저 정보를 추출하여
     * 닉네임, 약관 동의, 수면 정보 등을 포함한 새로운 User 객체를 빌드합니다.
     *
     * @param request 레지스터 토큰 및 기타 온보딩 정보를 포함한 요청 객체
     * @return 생성된 User 엔티티 객체
     */
    private User createUserFromRegisterToken(OnboardUserRequest request) {
        String token = request.registerToken();
        jwtUtil.validateToken(token);

        return User.builder()
                .name(jwtUtil.getClaimFromToken(token, "name", String.class))
                .email(jwtUtil.getClaimFromToken(token, "email", String.class))
                .nickname(request.nickname())
                .provider(jwtUtil.getClaimFromToken(token, "provider", String.class))
                .providerId(jwtUtil.getClaimFromToken(token, "providerId", String.class))
                .servicePolicyAgreement(request.servicePolicyAgreement())
                .privacyPolicyAgreement(request.privacyPolicyAgreement())
                .marketingPolicyAgreement(request.marketingPolicyAgreement())
                .sleepStartTime(request.sleepStartTime())
                .sleepEndTime(request.sleepEndTime())
                .language(request.language())
                .build();
    }

    /**
     * 유저 정보 조회 메서드.
     *
     * 인증된 유저의 프로필 정보를 반환합니다.
     *
     * @return 유저 프로필 응답 데이터
     */
    @Transactional(readOnly = true)
    public GetUserProfileResponse getUserProfile() {
        User user = userRepository.findById(UserAuthorizationUtil.getLoginUserId())
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER));
        return GetUserProfileResponse.of(user);
    }

    /**
     * 유저 정보 수정 메서드.
     *
     * 인증된 유저의 닉네임 or 언어를 수정합니다.
     * 수정된 닉네임은 길이 제한을 검증합니다.
     *
     * @param updateUserProfileRequest 유저 정보 수정 요청 데이터
     */
    @Transactional
    public void updateUserProfile(UpdateUserProfileRequest updateUserProfileRequest) {
        User user = userRepository.findById(UserAuthorizationUtil.getLoginUserId())
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER));
        Optional.ofNullable(updateUserProfileRequest.nickname()).ifPresent(user::updateNickName);
        Optional.ofNullable(updateUserProfileRequest.language()).ifPresent(user::updateLanguage);
        userRepository.save(user);
    }

    /**
     * 유저 서비스 탈퇴 메서드.
     *
     * 인증된 유저의 계정을 삭제합니다.
     *
     */
    @Transactional
    public void withdrawService() {
        User user = userRepository.findById(UserAuthorizationUtil.getLoginUserId())
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER));
        userRepository.withdraw(user);
    }

    /**
     * 유저 약관 동의 여부 조회 메서드.
     *
     * 인증된 사용자의 필수 및 선택 약관 동의 상태를 반환합니다.
     * 값이 null일 경우 기본값(false)을 반환합니다.
     *
     * @return 유저 약관 동의 여부 응답 객체
     */
    @Transactional(readOnly = true)
    public GetUserPolicyAgreementResponse getUserPolicyAgreement() {
        User user = userRepository.findById(UserAuthorizationUtil.getLoginUserId())
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER));
        return GetUserPolicyAgreementResponse.from(user);
    }

    /**
     * 유저 약관 동의 여부 수정 메서드.
     *
     * 인증된 유저의 서비스 이용약관, 개인정보 수집 및 이용 동의, 마케팅 정보 수신 동의 상태를 업데이트합니다.
     * 모든 필드는 필수 값이며, `@NotNull` 검증을 거칩니다.
     *
     * @param request 약관 동의 여부 수정 요청 데이터
     */
    @Transactional
    public void updateUserPolicyAgreement(UpdateUserPolicyAgreementRequest request) {
        User user = userRepository.findById(UserAuthorizationUtil.getLoginUserId())
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER));
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
     * @return 유저 수면 시간 응답 데이터 (시작 시간 및 종료 시간 포함)
     */
    @Transactional(readOnly = true)
    public GetUserSleepTimeResponse getUserSleepTime() {
        User user = userRepository.findById(UserAuthorizationUtil.getLoginUserId())
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER));
        return GetUserSleepTimeResponse.from(user);
    }

    /**
     * 유저 수면 시간 수정 메서드.
     *
     * 인증된 사용자의 수면 시작 시간과 종료 시간을 업데이트합니다.
     *
     * @param request 수면 시간 수정 요청 데이터 (필수 값)
     */
    @Transactional
    public void updateUserSleepTime(UpdateUserSleepTimeRequest request) {
        User user = userRepository.findById(UserAuthorizationUtil.getLoginUserId())
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER));
        user.updateSleepStartTime(request.sleepStartTime());
        user.updateSleepEndTime(request.sleepEndTime());
        userRepository.save(user);
    }
}
