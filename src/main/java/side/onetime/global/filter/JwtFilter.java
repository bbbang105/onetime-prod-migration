package side.onetime.global.filter;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.getTokenFromHeader(request.getHeader("Authorization"));
        jwtUtil.validateToken(token);
        Long userId = jwtUtil.getClaimFromToken(token, "userId", Long.class);
        setAuthentication(userId);

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
     * 특정 경로에 대해 JWT Filter를 생략합니다.
     *
     * @param request HTTP 요청 객체
     * @return true일 경우 해당 요청에 대해 필터를 적용하지 않음
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // 공통 prefix
        boolean isGet = method.equals("GET");
        boolean isPost = method.equals("POST");

        return path.equals("/actuator/health") ||

                // 로그인 없이 접근 가능한 공통 API
                path.startsWith("/api/v1/admin") ||
                path.startsWith("/api/v1/banners") ||
                path.startsWith("/api/v1/members") ||
                path.startsWith("/api/v1/tokens") ||
                path.startsWith("/api/v1/urls") ||

                // 이벤트 관련
                (isPost && path.equals("/api/v1/events")) ||
                (isGet && path.matches("/api/v1/events/[^/]+$")) ||
                (isGet && path.matches("/api/v1/events/[^/]+/participants")) ||
                (isGet && path.matches("/api/v1/events/[^/]+/most")) ||
                (isGet && path.matches("/api/v1/events/qr/[^/]+")) ||

                // 요일 스케줄 등록/조회 (비로그인)
                (isPost && path.equals("/api/v1/schedules/day")) ||
                (isGet && path.matches("/api/v1/schedules/day/[^/]+$") && !path.endsWith("/user")) ||
                (isGet && path.matches("/api/v1/schedules/day/[^/]+/\\d+$")) ||
                (isGet && path.equals("/api/v1/schedules/day/action-filtering")) ||

                // 날짜 스케줄 등록/조회 (비로그인)
                (isPost && path.equals("/api/v1/schedules/date")) ||
                (isGet && path.matches("/api/v1/schedules/date/[^/]+$") && !path.endsWith("/user")) ||
                (isGet && path.matches("/api/v1/schedules/date/[^/]+/\\d+$")) ||
                (isGet && path.equals("/api/v1/schedules/date/action-filtering"));
    }
}
