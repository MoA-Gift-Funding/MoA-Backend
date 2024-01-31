package moa.funding.application.command;

import moa.funding.domain.Price;

public record FundingFinishCommand(
        Long fundingId,
        Long memberId,
        Price amount
) {
    public FundingFinishCommand(Long fundingId, Long memberId, String amount) {
        this(fundingId, memberId, Price.from(amount));
    }
}
