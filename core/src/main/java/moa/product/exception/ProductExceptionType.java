package moa.product.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum ProductExceptionType implements MoaExceptionType {

    NOT_FOUND_PRODUCT(NOT_FOUND, "상품을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private String detailMessage;

    ProductExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
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
