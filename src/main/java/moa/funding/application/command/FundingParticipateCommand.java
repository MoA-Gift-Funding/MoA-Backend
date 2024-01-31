package moa.funding.application.command;

import moa.funding.domain.Price;

public record FundingParticipateCommand(
        Long fundingId,
        Long memberId,
        Price amount,
        String message
) {
    public FundingParticipateCommand(Long fundingId, Long memberId, String amount, String message) {
        this(fundingId, memberId, Price.from(amount), message);
    }
}
