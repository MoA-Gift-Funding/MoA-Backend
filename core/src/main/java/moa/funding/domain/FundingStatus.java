package moa.funding.domain;

import lombok.Getter;

@Getter
public enum FundingStatus {

    PROCESSING,
    CANCELLED,  // 사용자가 취소함
    STOPPED,  // 상품 공급 중단으로 중단됨
    COMPLETE,
    EXPIRED,
    REFUND_COMPLETE,
    ;
}
