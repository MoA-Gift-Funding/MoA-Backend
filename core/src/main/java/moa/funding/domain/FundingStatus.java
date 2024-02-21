package moa.funding.domain;

import lombok.Getter;

@Getter
public enum FundingStatus {

    PROCESSING("진행중"),
    WAITING_ORDER("수령 대기"),
    COMPLETE_ORDER("수령 완료"),
    CANCELLED("취소"),
    STOPPED("펀딩 중단"),
    EXPIRED("만료"),
    ;

    private final String description;

    FundingStatus(String description) {
        this.description = description;
    }
}
