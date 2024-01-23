package moa.funding.exception;

import org.springframework.http.HttpStatus;

import moa.global.exception.MoaExceptionType;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum FundingExceptionType implements MoaExceptionType {

    NOT_FOUND_FUNDING(NOT_FOUND, "펀딩을 찾을 수 없습니다."),
    INVALID_MINIMUM_PRICE(BAD_REQUEST, "최소 펀딩 금액이 기준 금액보다 작습니다."),
    INVALID_MAXIMUM_PRICE(BAD_REQUEST, "최대 펀딩 금액이 기준 금액보다 작습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    FundingExceptionType(HttpStatus httpStatus, String message) {
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
