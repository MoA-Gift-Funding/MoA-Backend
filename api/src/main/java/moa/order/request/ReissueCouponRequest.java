package moa.order.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import moa.order.application.command.CouponReissueCommand;

public record ReissueCouponRequest(
        @Schema(description = "쿠폰 받을 전화번호", example = "010-1234-5678")
        @NotBlank String phoneNumber
) {
    public CouponReissueCommand toCommand(Long memberId, Long orderId) {
        return new CouponReissueCommand(memberId, orderId, phoneNumber);
    }
}
