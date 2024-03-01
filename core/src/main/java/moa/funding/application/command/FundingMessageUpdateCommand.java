package moa.funding.application.command;

import moa.funding.domain.MessageVisibility;

public record FundingMessageUpdateCommand(
        Long memberId,
        Long messageId,
        String message,
        MessageVisibility visibility
) {
}
