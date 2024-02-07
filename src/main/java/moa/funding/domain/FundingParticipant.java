package moa.funding.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.funding.domain.ParticipantStatus.CANCEL;
import static moa.funding.domain.ParticipantStatus.PARTICIPATING;
import static moa.funding.exception.FundingExceptionType.ALREADY_CANCEL_PARTICIPATING;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(name = "message")
    private String message;

    @Enumerated(STRING)
    private ParticipantStatus status;

    public FundingParticipant(Member member, Funding funding, TossPayment payment, String message) {
        this.member = member;
        this.funding = funding;
        this.tossPayment = payment;
        this.amount = payment.getTotalAmount();
        this.message = message;
        this.status = PARTICIPATING;
    }

    public void cancel() {
        if (status == CANCEL) {
            throw new FundingException(ALREADY_CANCEL_PARTICIPATING);
        }
        this.status = CANCEL;
        tossPayment.cancel();
    }
}
