package moa.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(MoaException.class)
    ResponseEntity<ExceptionResponse> handleException(HttpServletRequest request, MoaException e) {
        MoaExceptionType type = e.getExceptionType();
        log.info("잘못된 요청이 들어왔습니다. URI: {},  내용:  {}", request.getRequestURI(), type.getMessage());
        return ResponseEntity.status(type.getHttpStatus())
                .body(new ExceptionResponse(type.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ExceptionResponse> handleException(HttpServletRequest request, Exception e) {
        log.error("예상하지 못한 예외가 발생했습니다. URI: {}, ", request.getRequestURI(), e);
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse("알 수 없는 오류가 발생했습니다."));
    }
}
