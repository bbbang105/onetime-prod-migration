package side.onetime.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import side.onetime.auth.service.CustomUserDetailsService;
import side.onetime.util.JwtUtil;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 요청을 처리하며 JWT 검증 및 인증 설정을 수행합니다.
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain  필터 체인 객체
     * @throws ServletException 서블릿 예외 발생 시
     * @throws IOException      입출력 예외 발생 시
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // ✅ OPTIONS 요청일 경우 필터를 건너뛰고 바로 다음 필터 실행
            if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = jwtUtil.getTokenFromHeader(request.getHeader("Authorization"));
            jwtUtil.validateToken(token);
            Long userId = jwtUtil.getClaimFromToken(token, "userId", Long.class);
            setAuthentication(userId);

        } catch (Exception e) {
            log.error("JWT validation failed: " + e.getMessage());

            // ✅ 직접 401 응답 반환
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Invalid or expired token\", \"status\": 401}");
            response.getWriter().flush();
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 인증 정보를 SecurityContext에 설정합니다.
     *
     * @param userId 인증된 사용자의 ID
     */
    private void setAuthentication(Long userId) {
        UserDetails userDetails = customUserDetailsService.loadUserByUserId(userId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 특정 경로에 대해 JWT 검증을 생략합니다.
     *
     * @param request HTTP 요청 객체
     * @return true일 경우 해당 요청에 대해 필터를 적용하지 않음
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/users/onboarding");
    }
}
