package moa.funding.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum FundingExceptionType implements MoaExceptionType {

    NOT_FOUND_FUNDING(NOT_FOUND, "펀딩을 찾을 수 없습니다."),
    NOT_FOUND_PARTICIPANT(BAD_REQUEST, "펀딩 참여자를 찾을 수 없습니다."),
    NOT_FOUND_MESSAGE(BAD_REQUEST, "펀딩 메세지를 찾을 수 없습니다."),
    INVALID_FUNDING_END_DATE(BAD_REQUEST, "종료일이 현재 날짜보다 이전입니다."),
    EXCEED_FUNDING_MAX_PERIOD(BAD_REQUEST, "펀딩의 최대 기간은 한달입니다."),
    FUNDING_MAXIMUM_AMOUNT_LESS_THAN_MINIMUM(BAD_REQUEST, "최대 펀딩 금액이 기준 금액보다 작습니다."),
    FUNDING_PRODUCT_PRICE_UNDER_MINIMUM_PRICE(BAD_REQUEST, "펀딩할 상품의 가격은 최소 금액 이상이어야 합니다."),
    FUNDING_PRODUCT_PRICE_LESS_THAN_MAXIMUM_AMOUNT(BAD_REQUEST, "상품 금액이 펀딩 가능 최대 금액보다 작습니다."),
    CAN_NOT_VISIBLE_FUNDING(FORBIDDEN, "해당 펀딩을 조회할 수 없습니다."),
    EXCEEDED_POSSIBLE_FUNDING_AMOUNT(BAD_REQUEST, "펀딩 가능한 최대 금액을 초과했습니다."),
    MUST_FUNDING_MORE_THAN_MINIMUM_AMOUNT(BAD_REQUEST, "최소 금액 이상 펀딩해주서야 합니다."),
    NOT_PROCESSING_FUNDING(BAD_REQUEST, "진행중인 펀딩이 아닙니다."),
    OWNER_CANNOT_PARTICIPATE_FUNDING(BAD_REQUEST, "펀딩 주인은 자신의 펀딩에 참여할 수 없습니다."),
    NO_AUTHORITY_FOR_FUNDING(FORBIDDEN, "펀딩 주인만 가능한 요청입니다."),
    NO_AUTHORITY_FOR_MESSAGE(FORBIDDEN, "메세지에 대한 권한이 없습니다."),
    DIFFERENT_FROM_FUNDING_REMAIN_AMOUNT(BAD_REQUEST, "펀딩의 남은 금액과 결제하려는 금액이 다릅니다."),
    PROCESSING_OR_EXPIRED_FUNDING_CAN_BE_CANCELLED(BAD_REQUEST, "진행중이거나 만료된 펀딩만 취소 가능합니다."),
    PROCESSING_OR_EXPIRED_FUNDING_CAN_BE_FINISHED(BAD_REQUEST, "진행중이거나 만료된 펀딩만 끝내기가 가능합니다."),
    ALREADY_CANCEL_PARTICIPATING(BAD_REQUEST, "이미 참여 취소한 펀딩입니다."),
    NO_AUTHORITY_CANCEL_PARTICIPATE(FORBIDDEN, "펀딩 참여를 취소할 권한이 없습니다"),
    PARTICIPATE_CANCEL_ONLY_PERMIT_PROCESSING_FUNDING(BAD_REQUEST, "진행중인 펀딩에 대해서만 참여 취소가 가능합니다.");

    private final HttpStatus httpStatus;
    private final String message;
    private String detailMessage;

    FundingExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public FundingExceptionType withDetail(String detailMessage) {
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
