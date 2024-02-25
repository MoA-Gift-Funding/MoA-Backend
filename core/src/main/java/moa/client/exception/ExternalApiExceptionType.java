package moa.client.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum ExternalApiExceptionType implements MoaExceptionType {

    EXTERNAL_API_EXCEPTION(INTERNAL_SERVER_ERROR, "외부 API 호출 시 예외 발생"),
    ;

    private final String message;
    private HttpStatus httpStatus;
    private String detailMessage;

    ExternalApiExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public ExternalApiExceptionType setStatus(HttpStatus status) {
        this.httpStatus = status;
        return this;
    }

    @Override
    public ExternalApiExceptionType withDetail(String detailMessage) {
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
