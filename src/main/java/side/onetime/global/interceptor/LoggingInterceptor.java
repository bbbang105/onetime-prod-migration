package side.onetime.global.interceptor;

import static net.logstash.logback.argument.StructuredArguments.*;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    /**
     * 요청 전 처리 로직을 수행합니다.
     */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		request.setAttribute("startTime", System.currentTimeMillis());
		return true;
	}

    /**
     * 요청 완료 후 처리 로직을 수행합니다.
     */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		Long start = (Long) request.getAttribute(START_TIME);
		long duration = System.currentTimeMillis() - (start != null ? start : 0L);
		int status = response.getStatus();
		String method = request.getMethod();
		String uri = request.getRequestURI();

		if (status >= 400) {
			log.error("❌ Request failed",
				kv("http_method", method),
				kv("request_uri", uri),
				kv("http_status", status),
				kv("duration_ms", duration)
			);
		} else {
			log.info("✅ Request completed successfully",
				kv("http_method", method),
				kv("request_uri", uri),
				kv("http_status", status),
				kv("duration_ms", duration)
			);
		}
	}
}
