package moa.order.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum OrderExceptionType implements MoaExceptionType {

    NOT_FOUND_ORDER(NOT_FOUND, "주문을 찾을 수 없습니다."),
    NOT_FOUND_ORDER_TX(NOT_FOUND, "주문 트랜잭션 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private String detailMessage;

    OrderExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public OrderExceptionType withDetail(String detailMessage) {
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
