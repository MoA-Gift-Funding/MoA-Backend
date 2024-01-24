package moa.delivery.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum DeliveryExceptionType implements MoaExceptionType {

    NOT_FOUND_DELIVERY(NOT_FOUND, "배송지를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    DeliveryExceptionType(HttpStatus httpStatus, String message) {
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
