package moa.funding.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import moa.funding.application.command.FundingFinishCommand;

public record FundingFinishRequest(
        @Schema(description = "결재시 사용한 주문 Id")
        @NotBlank String paymentOrderId
) {
    public FundingFinishCommand toCommand(Long fundingId, Long memberId) {
        return new FundingFinishCommand(
                fundingId,
                memberId,
                paymentOrderId
        );
    }
}
