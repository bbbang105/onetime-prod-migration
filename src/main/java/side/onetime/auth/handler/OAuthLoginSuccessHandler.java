package side.onetime.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import side.onetime.auth.dto.GoogleUserInfo;
import side.onetime.auth.dto.KakaoUserInfo;
import side.onetime.auth.dto.NaverUserInfo;
import side.onetime.auth.dto.OAuth2UserInfo;
import side.onetime.domain.RefreshToken;
import side.onetime.domain.User;
import side.onetime.repository.RefreshTokenRepository;
import side.onetime.repository.UserRepository;
import side.onetime.util.JwtUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${jwt.redirect.access}")
    private String ACCESS_TOKEN_REDIRECT_URI;

    @Value("${jwt.redirect.register}")
    private String REGISTER_TOKEN_REDIRECT_URI;

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId(); // provider 추출

        OAuth2UserInfo oAuth2UserInfo = extractOAuth2UserInfo(token, provider);
        handleAuthentication(request, response, oAuth2UserInfo, provider);
    }

    // OAuth2UserInfo 추출
    private OAuth2UserInfo extractOAuth2UserInfo(OAuth2AuthenticationToken token, String provider) {
        switch (provider) {
            case "google":
                log.info("구글 로그인 요청");
                return new GoogleUserInfo(token.getPrincipal().getAttributes());
            case "kakao":
                log.info("카카오 로그인 요청");
                return new KakaoUserInfo(token.getPrincipal().getAttributes());
            case "naver":
                log.info("네이버 로그인 요청");
                return new NaverUserInfo((Map<String, Object>) token.getPrincipal().getAttributes().get("response"));
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다.");
        }
    }

    // 인증 처리
    private void handleAuthentication(HttpServletRequest request, HttpServletResponse response, OAuth2UserInfo oAuth2UserInfo, String provider) throws IOException {
        String providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();

        User existUser = userRepository.findByProviderId(providerId);

        if (existUser == null) {
            // 신규 유저 처리
            handleNewUser(request, response, provider, providerId, name, email);
        } else {
            // 기존 유저 처리
            handleExistingUser(request, response, existUser);
        }

        log.info("NAME : {}", name);
        log.info("PROVIDER : {}", provider);
        log.info("PROVIDER_ID : {}", providerId);
        log.info("EMAIL : {}", email);
    }

    // 신규 유저 처리
    private void handleNewUser(HttpServletRequest request, HttpServletResponse response, String provider, String providerId, String name, String email) throws IOException {
        log.info("신규 유저입니다.");
        String registerToken = jwtUtil.generateRegisterToken(provider, providerId, name, email, ACCESS_TOKEN_EXPIRATION_TIME);
        String redirectUri = String.format(REGISTER_TOKEN_REDIRECT_URI, registerToken, URLEncoder.encode(name, StandardCharsets.UTF_8));
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    // 기존 유저 처리
    private void handleExistingUser(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        log.info("기존 유저입니다.");
        Long userId = user.getId();

        // 액세스 & 리프레쉬 토큰 발급 및 저장
        String accessToken = jwtUtil.generateAccessToken(userId, ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = jwtUtil.generateRefreshToken(userId, REFRESH_TOKEN_EXPIRATION_TIME);
        saveRefreshToken(userId, refreshToken);

        // 리다이렉트 처리
        String redirectUri = String.format(ACCESS_TOKEN_REDIRECT_URI, accessToken, refreshToken);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    // Refresh Token 저장
    private void saveRefreshToken(Long userId, String refreshToken) {
        RefreshToken newRefreshToken = new RefreshToken(userId, refreshToken);
        refreshTokenRepository.save(newRefreshToken);
    }
}