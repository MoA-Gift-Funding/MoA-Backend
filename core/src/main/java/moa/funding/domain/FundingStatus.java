package moa.funding.domain;

import lombok.Getter;

@Getter
public enum FundingStatus {

    PROCESSING,
    CANCELLED,  // 사용자가 취소함
    STOPPED,  // 상품 공급 중단으로 중단됨
    COMPLETE,

    // 펀딩 기간 이후 7일동안 만료 상태 -> 금액 채우면 `완료`, 안채우고 7일이 지나거나, 취소하면 `취소`
    EXPIRED,
    ;
}
