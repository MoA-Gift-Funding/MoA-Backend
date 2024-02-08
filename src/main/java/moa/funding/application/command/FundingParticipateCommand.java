package moa.funding.application.command;

import moa.funding.domain.MessageVisibility;

public record FundingParticipateCommand(
        Long fundingId,
        Long memberId,
        String paymentOrderId,
        String message,
        MessageVisibility visible
) {
}
