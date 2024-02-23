package moa.order.domain;

public enum OrderStatus {

    WAITING_RECEIVE,  // 수령대기 (기본적으로 수령완료 상태이나, 쿠폰 발급시 문제가 생긴 경우 해당 상태)
    COMPLETE_RECEIVE,  // 수령완료
    REFUND,  // 환불
}
