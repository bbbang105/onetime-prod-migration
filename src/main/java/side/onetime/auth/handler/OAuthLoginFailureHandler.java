package side.onetime.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${jwt.redirect.access}")
    private String ACCESS_TOKEN_REDIRECT_URI;

    /**
     * 로그인 실패 처리 메서드.
     *
     * OAuth2 인증 실패 시 호출됩니다. 실패 원인을 로그로 기록하고,
     * 실패 정보를 포함한 URL로 리다이렉트를 수행합니다.
     *
     * @param request   HttpServletRequest 객체
     * @param response  HttpServletResponse 객체
     * @param exception AuthenticationException 객체 (인증 실패 원인)
     * @throws IOException 입출력 예외가 발생한 경우
     * @throws ServletException 서블릿 예외가 발생한 경우
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.error("LOGIN FAILED: {}", exception.getMessage());

        String redirectUri = String.format(ACCESS_TOKEN_REDIRECT_URI, "false", "", "");
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
