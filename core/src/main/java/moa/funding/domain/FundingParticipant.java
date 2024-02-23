package moa.funding.domain;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.funding.domain.ParticipantStatus.CANCEL;
import static moa.funding.domain.ParticipantStatus.PARTICIPATING;
import static moa.funding.exception.FundingExceptionType.ALREADY_CANCEL_PARTICIPATING;
import static moa.funding.exception.FundingExceptionType.NO_AUTHORITY_CANCEL_PARTICIPATE;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.funding.exception.FundingException;
import moa.global.domain.Price;
import moa.global.domain.RootEntity;
import moa.member.domain.Member;
import moa.pay.domain.TossPayment;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class FundingParticipant extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "funding_id")
    private Funding funding;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payment_id", unique = true)
    private TossPayment tossPayment;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private Price amount;

    @OneToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "funding_message_id")
    private FundingMessage fundingMessage;

    @Enumerated(STRING)
    private ParticipantStatus status;

    public FundingParticipant(
            Member member,
            Funding funding,
            TossPayment payment,
            String message,
            MessageVisibility messageVisible
    ) {
        this.member = member;
        this.funding = funding;
        this.tossPayment = payment;
        this.amount = payment.getTotalAmount();
        this.fundingMessage = new FundingMessage(member, funding.getMember(), message, messageVisible);
        this.status = PARTICIPATING;
    }

    public void validateMember(Member member) {
        if (!this.member.equals(member)) {
            throw new FundingException(NO_AUTHORITY_CANCEL_PARTICIPATE);
        }
    }

    public void cancel() {
        if (status == CANCEL) {
            throw new FundingException(ALREADY_CANCEL_PARTICIPATING);
        }
        this.status = CANCEL;
        tossPayment.cancel();
    }
}
