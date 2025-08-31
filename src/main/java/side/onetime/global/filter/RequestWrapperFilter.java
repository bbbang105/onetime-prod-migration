package side.onetime.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import side.onetime.global.wrapper.CustomHttpRequestWrapper;

import java.io.IOException;
import java.util.Objects;

@Component
public class RequestWrapperFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String contentType = httpRequest.getContentType();
            CustomHttpRequestWrapper wrapper = null;

            if (contentType != null && !contentType.startsWith("multipart/")) {
                wrapper = new CustomHttpRequestWrapper(httpRequest);
            }

            chain.doFilter(Objects.requireNonNullElse(wrapper, request), response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
