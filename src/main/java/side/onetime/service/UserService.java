package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.RefreshToken;
import side.onetime.domain.User;
import side.onetime.dto.user.request.OnboardUserRequest;
import side.onetime.dto.user.request.UpdateUserProfileRequest;
import side.onetime.dto.user.response.GetUserPolicyAgreementResponse;
import side.onetime.dto.user.response.GetUserProfileResponse;
import side.onetime.dto.user.response.OnboardUserResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.UserErrorStatus;
import side.onetime.repository.RefreshTokenRepository;
import side.onetime.repository.UserRepository;
import side.onetime.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int NICKNAME_LENGTH_LIMIT = 10;

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
     * 회원가입 및 초기 설정을 진행합니다. 제공된 레지스터 토큰을 이용하여 유저 정보를 검증하고 저장합니다.
     * 저장된 유저 정보를 기반으로 새로운 액세스 토큰과 리프레쉬 토큰을 생성하고 반환합니다.
     *
     * @param onboardUserRequest 온보딩 요청 데이터
     * @return 액세스 토큰 및 리프레쉬 토큰 응답 데이터
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

        if (onboardUserRequest.nickname().length() > NICKNAME_LENGTH_LIMIT) {
            throw new CustomException(UserErrorStatus._NICKNAME_TOO_LONG);
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .nickname(onboardUserRequest.nickname())
                .provider(provider)
                .providerId(providerId)
                .build();
        userRepository.save(user);
        Long userId = user.getId();

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
     * 인증된 유저의 닉네임을 수정합니다.
     * 수정된 닉네임은 길이 제한을 검증하며 저장됩니다.
     *
     * @param user 인증된 사용자 정보
     * @param updateUserProfileRequest 유저 정보 수정 요청 데이터
     */
    @Transactional
    public void updateUserProfile(User user, UpdateUserProfileRequest updateUserProfileRequest) {
        String nickname = updateUserProfileRequest.nickname();

        if (nickname.length() > NICKNAME_LENGTH_LIMIT) {
            throw new CustomException(UserErrorStatus._NICKNAME_TOO_LONG);
        }
        user.updateNickName(nickname);
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
}
