package moa.funding.domain;

import lombok.Getter;

@Getter
public enum FundingStatus {

    PREPARING("준비중"),
    PROCESSING("진행중"),
    DONE("완료"),
    CANCELLED("취소"),
    ;

    private final String description;

    FundingStatus(String description) {
        this.description = description;
    }
}
