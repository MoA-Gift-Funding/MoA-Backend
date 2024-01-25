package moa.address.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum DeliveryAddressExceptionType implements MoaExceptionType {

    NOT_FOUND_ADDRESS(NOT_FOUND, "배송지를 찾을 수 없습니다."),
    REQUIRED_DEFAULT_ADDRESS(BAD_REQUEST, "기본 주소지는 필수로 존재해야 합니다."),
    NO_AUTHORITY(FORBIDDEN, "배송지의 주인이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;

    DeliveryAddressExceptionType(HttpStatus httpStatus, String message) {
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
