package moa.funding.domain;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.funding.domain.FundingStatus.PROCESSING;
import static moa.funding.domain.ParticipantStatus.CANCEL;
import static moa.funding.domain.ParticipantStatus.CANCELLED_BY_FUND_OWNER;
import static moa.funding.domain.ParticipantStatus.PARTICIPATING;
import static moa.funding.exception.FundingExceptionType.ALREADY_CANCEL_PARTICIPATING;
import static moa.funding.exception.FundingExceptionType.FUNDING_IS_NOT_PROCESSING;
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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "funding_id", nullable = false)
    private Funding funding;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private TossPayment tossPayment;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private Price amount;

    @OneToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "funding_message_id", nullable = false)
    private FundingMessage fundingMessage;

    @Enumerated(STRING)
    @Column(nullable = false)
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
        this.status = PARTICIPATING;
        this.fundingMessage = new FundingMessage(
                member,
                funding.getMember(),
                message,
                messageVisible
        );
    }

    public void validateMember(Member member) {
        if (!this.member.equals(member)) {
            throw new FundingException(NO_AUTHORITY_CANCEL_PARTICIPATE);
        }
    }

    public boolean isParticipating() {
        return status == PARTICIPATING;
    }

    public void cancel() {
        validateCancel();
        validateFundingIsProcessing();
        this.status = CANCEL;
        tossPayment.pendingCancel("펀딩 참여를 희망하지 않음");
    }

    private void validateFundingIsProcessing() {
        if (funding.getStatus() == PROCESSING) {
            return;
        }
        throw new FundingException(FUNDING_IS_NOT_PROCESSING);
    }

    public void canceledByFundingOwner() {
        validateCancel();
        this.status = CANCELLED_BY_FUND_OWNER;
        tossPayment.pendingCancel("펀딩 생성자의 펀딩 취소로 인한 결제 취소");
    }

    private void validateCancel() {
        if (status == CANCEL || status == CANCELLED_BY_FUND_OWNER) {
            throw new FundingException(ALREADY_CANCEL_PARTICIPATING);
        }
    }
}
