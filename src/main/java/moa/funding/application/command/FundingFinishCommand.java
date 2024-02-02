package moa.funding.application.command;

public record FundingFinishCommand(
        Long fundingId,
        Long memberId,
        String paymentOrderId
) {
}
