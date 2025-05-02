package side.onetime.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import side.onetime.global.wrapper.CustomHttpRequestWrapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Map<String, String> pathVariables =
                    (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (pathVariables != null && !pathVariables.isEmpty()) {
                log.info("📦  [{}] {} \npathVars : {}", request.getMethod(), request.getRequestURI(), pathVariables);
            }
        }

        if (request.getParameterNames().hasMoreElements()) {
            log.info("📦️  [{}] {} \nqueryParams : {}", request.getMethod(), request.getRequestURI(), getRequestParams(request));
        }

        if (request instanceof CustomHttpRequestWrapper wrapper) {
            String body = new String(wrapper.getRequestBody());
            if (!body.isBlank()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Object json = mapper.readValue(body, Object.class);
                    String prettyBody = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                    log.info("📦 [{}] {} \nbody : {}", request.getMethod(), request.getRequestURI(), prettyBody);
                } catch (Exception e) {
                    log.info("📦 [{}] {} \nbody(raw) : {}", request.getMethod(), request.getRequestURI(), body);
                }
            }
        }

        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long start = (Long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - (start != null ? start : 0L);

        int status = response.getStatus();

        if (status == 500) {
            log.error("❌ [{}] {} request failed - {}ms | status=500", request.getMethod(), request.getRequestURI(), duration, ex);
        } else {
            log.info("✅ [{}] {} request completed - {}ms | status={}", request.getMethod(), request.getRequestURI(), duration, status);
        }
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            paramMap.put(paramName, request.getParameter(paramName));
        }

        return paramMap;
    }
}
