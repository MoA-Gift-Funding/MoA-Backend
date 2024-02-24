package moa.funding.domain;

public record FundingParticipateEvent(
        Long fundingId,
        FundingParticipant participant
) {
}
