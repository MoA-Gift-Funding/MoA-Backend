package moa.funding.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import moa.funding.application.command.FundingParticipateCommand;

public record FundingParticipateRequest(
        @Schema(description = "펀딩할 금액", example = "5000")
        @NotBlank String amount,

        @Schema(example = "말랑아 생일축하해~")
        @NotBlank String message
) {
    public FundingParticipateCommand toCommand(Long fundingId, Long memberId) {
        return new FundingParticipateCommand(
                fundingId,
                memberId,
                amount,
                message
        );
    }
}
