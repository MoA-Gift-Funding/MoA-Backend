package moa.order.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import moa.order.application.command.OrderPlaceCommand;

public record OrderPlaceRequest(
        @Schema(description = "주문 대기 상태의 펀딩 id")
        @NotNull Long fundingId
) {
    public OrderPlaceCommand toCommand(Long memberId) {
        return new OrderPlaceCommand(memberId, fundingId);
    }
}
