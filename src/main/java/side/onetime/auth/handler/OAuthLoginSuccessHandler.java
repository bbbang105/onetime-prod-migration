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
import side.onetime.auth.util.CookieUtil;
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

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * OAuth2 인증 성공 처리 메서드.
     *
     * 인증 성공 시 OAuth2AuthenticationToken을 기반으로 제공자 정보를 추출하고,
     * 인증 결과를 처리합니다.
     *
     * @param request  HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     * @param authentication 인증 성공 정보를 담은 객체
     * @throws IOException 인증 처리 중 발생할 수 있는 입출력 예외
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId(); // provider 추출

        OAuth2UserInfo oAuth2UserInfo = extractOAuth2UserInfo(token, provider);
        handleAuthentication(request, response, oAuth2UserInfo, provider);
    }

    /**
     * OAuth2 사용자 정보 추출 메서드.
     *
     * 제공자(provider)에 따라 적합한 OAuth2UserInfo 객체를 생성합니다.
     *
     * @param token OAuth2AuthenticationToken 객체
     * @param provider OAuth2 제공자 이름 (google, kakao, naver 등)
     * @return OAuth2UserInfo 객체
     */
    private OAuth2UserInfo extractOAuth2UserInfo(OAuth2AuthenticationToken token, String provider) {
        switch (provider) {
            case "google":
                return new GoogleUserInfo(token.getPrincipal().getAttributes());
            case "kakao":
                return new KakaoUserInfo(token.getPrincipal().getAttributes());
            case "naver":
                return new NaverUserInfo((Map<String, Object>) token.getPrincipal().getAttributes().get("response"));
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다.");
        }
    }

    /**
     * 인증 성공 처리 메서드.
     *
     * 인증된 사용자의 정보를 바탕으로 신규 또는 기존 사용자를 처리합니다.
     *
     * @param request  HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     * @param oAuth2UserInfo OAuth2 사용자 정보 객체
     * @param provider OAuth2 제공자 이름
     * @throws IOException 인증 처리 중 발생할 수 있는 입출력 예외
     */
    private void handleAuthentication(HttpServletRequest request, HttpServletResponse response, OAuth2UserInfo oAuth2UserInfo, String provider) throws IOException {
        String providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();

        User existUser = userRepository.findByProviderId(providerId);

        if (existUser == null) {
            handleNewUser(request, response, provider, providerId, name, email);
        } else {
            handleExistingUser(request, response, existUser);
        }
    }

    /**
     * 신규 유저 처리 메서드.
     *
     * OAuth2 인증을 통해 새로 가입한 사용자를 처리하고,
     * 회원가입 완료를 위한 리다이렉트를 수행합니다.
     *
     * @param request  HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     * @param provider OAuth2 제공자 이름
     * @param providerId 제공자 고유 ID
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @throws IOException 인증 처리 중 발생할 수 있는 입출력 예외
     */
    private void handleNewUser(HttpServletRequest request, HttpServletResponse response, String provider, String providerId, String name, String email) throws IOException {
        String registerToken = jwtUtil.generateRegisterToken(provider, providerId, name, email);
        String redirectUri = String.format(REGISTER_TOKEN_REDIRECT_URI, registerToken, URLEncoder.encode(name, StandardCharsets.UTF_8));
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    /**
     * 기존 유저 처리 메서드.
     *
     * OAuth2 인증을 통해 로그인한 기존 사용자를 처리하고,
     * 액세스 및 리프레쉬 토큰을 발급하여 리다이렉트를 수행합니다.
     *
     * @param request  HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     * @param user 기존 사용자 정보
     * @throws IOException 인증 처리 중 발생할 수 있는 입출력 예외
     */
    private void handleExistingUser(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        Long userId = user.getId();
        String browserId = jwtUtil.hashUserAgent(request.getHeader("User-Agent"));
        String accessToken = jwtUtil.generateAccessToken(userId, "USER");
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        refreshTokenRepository.save(new RefreshToken(userId, browserId, refreshToken));
        CookieUtil.setAuthCookies(response, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);

        String redirectUri = String.format(ACCESS_TOKEN_REDIRECT_URI, "true", accessToken);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
