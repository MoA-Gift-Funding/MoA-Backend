package moa.funding.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import moa.funding.application.command.FundingFinishCommand;

public record FundingFinishRequest(
        @Schema(description = "남은 펀딩 금액", example = "5000")
        @NotBlank String amount
) {
    public FundingFinishCommand toCommand(Long fundingId, Long memberId) {
        return new FundingFinishCommand(
                fundingId,
                memberId,
                amount
        );
    }
}
