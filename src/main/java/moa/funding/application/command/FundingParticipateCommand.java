package moa.funding.application.command;

public record FundingParticipateCommand(
        Long fundingId,
        Long memberId,
        String paymentOrderId,
        String message
) {
}
