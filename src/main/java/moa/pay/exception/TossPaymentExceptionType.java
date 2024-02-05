package moa.pay.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum TossPaymentExceptionType implements MoaExceptionType {

    NOT_FOUND_PAYMENT(NOT_FOUND, "결제 정보를 찾을 수 없습니다"),
    TOSS_API_ERROR(BAD_REQUEST, "토스 API 사용 중 오류가 발생했습니다."),
    PAYMENT_INVALID(BAD_REQUEST, "유효하지 않은 결제 정보입니다."),
    NO_AUTHORITY_PAYMENT(FORBIDDEN, "결제 정보에 대한 권한이 없습니다."),
    ALREADY_USED_PAYMENT(BAD_REQUEST, "이미 사용된 결제 정보입니다.");

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
