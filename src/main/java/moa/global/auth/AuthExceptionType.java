package moa.global.auth;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum AuthExceptionType implements MoaExceptionType {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않았습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    AuthExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
