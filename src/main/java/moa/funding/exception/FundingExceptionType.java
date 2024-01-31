package moa.funding.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum FundingExceptionType implements MoaExceptionType {

    NOT_FOUND_FUNDING(NOT_FOUND, "펀딩을 찾을 수 없습니다."),
    INVALID_END_DATE(BAD_REQUEST, "종료일이 현재 날짜보다 이전입니다."),
    INVALID_FUNDING_STATUS(BAD_REQUEST, "펀딩 상태가 유효하지 않습니다."),
    MAXIMUM_AMOUNT_LESS_THAN_MINIMUM(BAD_REQUEST, "최대 펀딩 금액이 기준 금액보다 작습니다."),
    PRODUCT_PRICE_UNDER_MINIMUM_PRICE(BAD_REQUEST, "펀딩할 상품의 가격은 최소 금액 이상이어야 합니다."),
    PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT(BAD_REQUEST, "상품 금액이 펀딩 가능 최대 금액보다 작습니다."),
    CAN_NOT_VISIBLE_FUNDING(FORBIDDEN, "해당 펀딩을 조회할 수 없습니다."),
    ALREADY_PARTICIPATED(BAD_REQUEST, "이미 참여한 펀딩입니다."),
    EXCEEDED_POSSIBLE_AMOUNT(BAD_REQUEST, "펀딩 가능한 최대 금액을 초과했습니다."),
    UNDER_MINIMUM_AMOUNT(BAD_REQUEST, "최소 금액 이상 펀딩해주서야 합니다."),
    NOT_PROCESSING(BAD_REQUEST, "진행중인 펀딩이 아닙니다."),
    OWNER_CANNOT_PARTICIPATE(BAD_REQUEST, "펀딩 주인은 자신의 펀딩에 참여할 수 없습니다."),
    NO_AUTHORITY_FINISH(FORBIDDEN, "펀딩 주인만 펀딩을 끝낼 수 있습니다."),
    DIFFERENT_FROM_REMAIN_AMOUNT(BAD_REQUEST, "펀딩의 남은 금액과 결제하려는 금액이 다릅니다.");

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
