package moa.pay.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum TossPaymentExceptionType implements MoaExceptionType {

    NOT_FOUND_PAYMENT(NOT_FOUND, "결제 정보를 찾을 수 없습니다"),
    PAYMENT_INVALID(BAD_REQUEST, "유효하지 않은 결제 정보입니다."),
    NO_AUTHORITY_PAYMENT(FORBIDDEN, "결제 정보에 대한 권한이 없습니다."),
    UNAVAILABLE_PAYMENT(BAD_REQUEST, "사용할 수 없는 결제 정보입니다."),
    ALREADY_CANCELED_PAYMENT(BAD_REQUEST, "이미 취소된 결제입니다."),
    ONLY_CANCEL_PENDING_PAYMENT(INTERNAL_SERVER_ERROR, "취소 대기 상태가 아닌 결제를 취소하려 했습니다."),
    ;

    private final String message;
    private HttpStatus httpStatus;
    private String detailMessage;

    TossPaymentExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public TossPaymentExceptionType setStatus(HttpStatus status) {
        this.httpStatus = status;
        return this;
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
