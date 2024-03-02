package moa.funding.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import moa.funding.application.command.FundingParticipateCommand;
import moa.funding.domain.MessageVisibility;

public record FundingParticipateRequest(
        @Schema(description = "결재시 사용한 주문 Id")
        @NotBlank String paymentOrderId,

        @Schema(example = "말랑아 생일축하해~")
        @NotBlank String message,

        @Schema(example = "메시지 공개 여부")
        @NotNull MessageVisibility visibility
) {
    public FundingParticipateCommand toCommand(Long fundingId, Long memberId) {
        return new FundingParticipateCommand(
                fundingId,
                memberId,
                paymentOrderId,
                message,
                visibility
        );
    }
}
