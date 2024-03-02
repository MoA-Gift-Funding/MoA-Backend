package moa.funding.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import moa.funding.application.command.FundingMessageUpdateCommand;
import moa.funding.domain.MessageVisibility;

public record FundingMessageUpdateRequest(
        @Schema(example = "말랑아 생일축하해~")
        @NotBlank String message,

        @Schema(example = "메시지 공개 여부")
        @NotNull MessageVisibility visibility
) {
    public FundingMessageUpdateCommand toCommand(Long memberId, Long messageId) {
        return new FundingMessageUpdateCommand(
                memberId,
                messageId,
                message,
                visibility
        );
    }
}
