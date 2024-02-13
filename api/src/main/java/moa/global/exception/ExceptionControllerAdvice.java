package moa.global.exception;

import static moa.global.log.RequestLoggingFilter.REQUEST_ID;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice {

    @ExceptionHandler({MoaException.class, MethodArgumentNotValidException.class})
    ResponseEntity<ExceptionResponse> handleException(HttpServletRequest request, MoaException e) {
        MoaExceptionType type = e.getExceptionType();
        log.info("[{}] 잘못된 요청이 들어왔습니다. uri: {} {},  내용:  {}",
                MDC.get(REQUEST_ID), request.getMethod(), request.getRequestURI(), type.getMessage());
        log.info("[{}] request header: {}", MDC.get(REQUEST_ID), getHeaders(request));
        log.info("[{}] request query string: {}", MDC.get(REQUEST_ID), getQueryString(request));
        log.info("[{}] request body: {}", MDC.get(REQUEST_ID), getRequestBody(request));
        return ResponseEntity.status(type.getHttpStatus())
                .body(new ExceptionResponse(type.name(), type.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ExceptionResponse> handleException(HttpServletRequest request, Exception e) {
        log.error("[{}] 예상하지 못한 예외가 발생했습니다. uri: {} {}, ",
                MDC.get(REQUEST_ID), request.getMethod(), request.getRequestURI(), e);
        log.info("[{}] request header: {}", MDC.get(REQUEST_ID), getHeaders(request));
        log.info("[{}] request body: {}", MDC.get(REQUEST_ID), getRequestBody(request));
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse("INTERNAL_EXCEPTION", "알 수 없는 오류가 발생했습니다."));
    }

    private Map<String, Object> getHeaders(HttpServletRequest request) {
        Map<String, Object> headerMap = new HashMap<>();
        Enumeration<String> headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = headerArray.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }

    private String getQueryString(HttpServletRequest httpRequest) {
        String queryString = httpRequest.getQueryString();
        if (queryString == null) {
            return " - ";
        }
        return queryString;
    }

    private String getRequestBody(HttpServletRequest request) {
        var wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper == null) {
            return " - ";
        }
        try {
            // body 가 읽히지 않고 예외처리 되는 경우에 캐시하기 위함
            wrapper.getInputStream().readAllBytes();
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length == 0) {
                return " - ";
            }
            return new String(buf, wrapper.getCharacterEncoding());
        } catch (Exception e) {
            return " - ";
        }
    }
}
