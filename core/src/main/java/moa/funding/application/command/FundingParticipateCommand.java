package moa.funding.application.command;

import moa.funding.domain.Funding;
import moa.funding.domain.FundingParticipant;
import moa.funding.domain.MessageVisibility;
import moa.member.domain.Member;
import moa.pay.domain.TossPayment;

public record FundingParticipateCommand(
        Long fundingId,
        Long memberId,
        String paymentOrderId,
        String message,
        MessageVisibility visible
) {
    public FundingParticipant toParticipant(Member member, Funding funding, TossPayment payment) {
        return new FundingParticipant(member, funding, payment, message, visible);
    }
}
