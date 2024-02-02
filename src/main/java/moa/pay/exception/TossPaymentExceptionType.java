package moa.pay.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum TossPaymentExceptionType implements MoaExceptionType {

    PAYMENT_ERROR(BAD_REQUEST, "결제 오류가 발생했습니다."),
    PAYMENT_INVALID(BAD_REQUEST, "유효하지 않은 결제정보입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private String detailMessage;

    TossPaymentExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public TossPaymentExceptionType withDetail(String detailMessage) {
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
