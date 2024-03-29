package moa.auth;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum AuthExceptionType implements MoaExceptionType {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않았습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private String detailMessage;

    AuthExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public AuthExceptionType withDetail(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        if (detailMessage == null) {
            return message;
        }
        return MESSAGE_FORMAT.formatted(message, detailMessage).strip();
    }
}
