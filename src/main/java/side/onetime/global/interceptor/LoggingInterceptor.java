package side.onetime.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long now = System.currentTimeMillis();
        request.setAttribute(START_TIME, now);

        log.info("➡️  [{}] {} 요청 시작", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long start = (Long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - (start != null ? start : 0L);

        int status = response.getStatus();

        log.info("✅ [{}] {} 요청 완료 - {}ms | status={}", request.getMethod(), request.getRequestURI(), duration, status);
    }
}
