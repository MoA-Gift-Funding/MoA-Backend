package moa.funding.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import moa.funding.application.command.FundingParticipateCancelCommand;

public record FundingParticipateCancelRequest(
        @Schema(example = "참가자 id")
        @NotNull Long fundingParticipantId
) {
    public FundingParticipateCancelCommand toCommand(Long memberId) {
        return new FundingParticipateCancelCommand(
                memberId,
                fundingParticipantId
        );
    }
}
