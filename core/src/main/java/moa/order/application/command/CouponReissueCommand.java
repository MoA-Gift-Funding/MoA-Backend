package moa.order.application.command;

public record CouponReissueCommand(
        Long memberId,
        Long orderId
) {
}
