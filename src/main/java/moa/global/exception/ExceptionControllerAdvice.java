package moa.global.exception;

import static moa.global.log.RequestLoggingFilter.REQUEST_ID;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
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
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler({MoaException.class, MethodArgumentNotValidException.class})
    ResponseEntity<ExceptionResponse> handleException(HttpServletRequest request, MoaException e) {
        MoaExceptionType type = e.getExceptionType();
        log.info("[{}] 잘못된 요청이 들어왔습니다. uri: {} {},  내용:  {}",
                MDC.get(REQUEST_ID), request.getMethod(), request.getRequestURI(), type.getMessage());
        log.info("[{}] request header: {}", MDC.get(REQUEST_ID), getHeaders(request));
        log.info("[{}] request body: {}", MDC.get(REQUEST_ID), getRequestBody(request));
        return ResponseEntity.status(type.getHttpStatus())
                .body(new ExceptionResponse(type.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ExceptionResponse> handleException(HttpServletRequest request, Exception e) {
        log.error("[{}] 예상하지 못한 예외가 발생했습니다. uri: {} {}, ",
                MDC.get(REQUEST_ID), request.getMethod(), request.getRequestURI(), e);
        log.info("[{}] request header: {}", MDC.get(REQUEST_ID), getHeaders(request));
        log.info("[{}] request body: {}", MDC.get(REQUEST_ID), getRequestBody(request));
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse("알 수 없는 오류가 발생했습니다."));
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

    private String getRequestBody(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return " - ";
                }
            }
        }
        return " - ";
    }
}
