package moa.funding.application.command;

public record FundingParticipateCancelCommand(
        Long memberId,
        Long fundingId,
        Long fundingParticipantId
) {
}
