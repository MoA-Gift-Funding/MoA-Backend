package moa.global.log;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class RequestLoggingFilter implements Filter {

    public static final String REQUEST_ID = "requestId";

    private final ObjectProvider<PathMatcher> pathMatcherProvider;
    private final Set<String> setIgnoredUrlPatterns = new HashSet<>();

    public void setIgnoredUrlPatterns(String... ignoredUrlPatterns) {
        this.setIgnoredUrlPatterns.addAll(Arrays.asList(ignoredUrlPatterns));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (CorsUtils.isPreFlightRequest(httpRequest) || isIgnoredUrl(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper cachedResponse = new ContentCachingResponseWrapper(
                (HttpServletResponse) response);

        StopWatch stopWatch = new StopWatch();
        try {
            MDC.put(REQUEST_ID, getRequestId(httpRequest));
            stopWatch.start();
            log.info("[{}] request start [uri: {} {}]",
                    MDC.get(REQUEST_ID), httpRequest.getMethod(), httpRequest.getRequestURI());
            chain.doFilter(cachedRequest, cachedResponse);
        } finally {
            stopWatch.stop();
            log.info("[{}] response body: {}", MDC.get(REQUEST_ID), getResponseBody(cachedResponse));
            log.info("[{}] request end [time: {}ms]", MDC.get(REQUEST_ID), stopWatch.getTotalTimeMillis());
            MDC.clear();
        }
    }

    private boolean isIgnoredUrl(HttpServletRequest request) {
        PathMatcher pathMatcher = this.pathMatcherProvider.getIfAvailable();
        Objects.requireNonNull(pathMatcher);
        return setIgnoredUrlPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }

    private String getRequestId(HttpServletRequest httpRequest) {
        String requestId = httpRequest.getHeader("X-Request-ID");
        if (ObjectUtils.isEmpty(requestId)) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        return requestId;
    }

    private String getResponseBody(HttpServletResponse response) throws IOException {
        String payload = null;
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            wrapper.setCharacterEncoding("UTF-8");
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                payload = new String(buf, wrapper.getCharacterEncoding());
                wrapper.copyBodyToResponse();
            }
        }
        return null == payload ? " - " : payload;
    }
}
