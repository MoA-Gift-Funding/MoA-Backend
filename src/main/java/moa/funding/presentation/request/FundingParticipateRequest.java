package moa.funding.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import moa.funding.application.command.FundingParticipateCommand;

public record FundingParticipateRequest(
        @Schema(description = "결재시 사용한 주문 Id")
        @NotBlank String paymentOrderId,

        @Schema(example = "말랑아 생일축하해~")
        @NotBlank String message,

        @Schema(example = "메시지 공개 여부")
        boolean visible
) {
    public FundingParticipateCommand toCommand(Long fundingId, Long memberId) {
        return new FundingParticipateCommand(
                fundingId,
                memberId,
                paymentOrderId,
                message,
                visible
        );
    }
}
