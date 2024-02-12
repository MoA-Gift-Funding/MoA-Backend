package moa.funding.domain;

import lombok.Getter;

@Getter
public enum FundingStatus {

    PROCESSING("진행중"),
    COMPLETED("완료"),
    CANCELLED("취소"),
    ;

    private final String description;

    FundingStatus(String description) {
        this.description = description;
    }
}
