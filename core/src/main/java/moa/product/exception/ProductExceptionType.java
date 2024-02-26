package moa.product.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum ProductExceptionType implements MoaExceptionType {

    NOT_FOUND_PRODUCT(NOT_FOUND, "상품을 찾을 수 없습니다."),
    COUPONS_CANNOT_BE_REISSUED(BAD_REQUEST, "쿠폰을 재발급할 수 없습니다.");

    private final String message;
    private HttpStatus httpStatus;
    private String detailMessage;

    ProductExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public ProductExceptionType setStatus(HttpStatus status) {
        this.httpStatus = status;
        return this;
    }

    @Override
    public ProductExceptionType withDetail(String detailMessage) {
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
