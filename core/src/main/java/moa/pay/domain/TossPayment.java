package moa.pay.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.pay.domain.TossPaymentStatus.CANCELED;
import static moa.pay.domain.TossPaymentStatus.PENDING_CANCEL;
import static moa.pay.domain.TossPaymentStatus.UNUSED;
import static moa.pay.domain.TossPaymentStatus.USED;
import static moa.pay.exception.TossPaymentExceptionType.ALREADY_CANCELED_PAYMENT;
import static moa.pay.exception.TossPaymentExceptionType.NO_AUTHORITY_PAYMENT;
import static moa.pay.exception.TossPaymentExceptionType.ONLY_CANCEL_PENDING_PAYMENT;
import static moa.pay.exception.TossPaymentExceptionType.UNAVAILABLE_PAYMENT;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.global.domain.Price;
import moa.global.domain.RootEntity;
import moa.pay.exception.TossPaymentException;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class TossPayment extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private String orderName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_amount"))
    private Price totalAmount;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(STRING)
    @Column(nullable = false)
    private TossPaymentStatus status;

    @Embedded
    @AttributeOverride(name = "reason", column = @Column(name = "cancel_reason"))
    private TossPaymentCancel cancel;

    public TossPayment(String paymentKey, String orderId, String orderName, String totalAmount, Long memberId) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.totalAmount = Price.from(totalAmount);
        this.memberId = memberId;
        this.status = UNUSED;
    }

    public void use(Long memberId) {
        if (status != UNUSED) {
            throw new TossPaymentException(UNAVAILABLE_PAYMENT
                    .withDetail("이미 사용되었거나 취소된 결제 정보입니다."));
        }
        if (this.memberId != memberId) {
            throw new TossPaymentException(NO_AUTHORITY_PAYMENT);
        }
        this.status = USED;
    }

    public void pendingCancel(String reason) {
        if (status == CANCELED) {
            throw new TossPaymentException(ALREADY_CANCELED_PAYMENT);
        }
        this.status = PENDING_CANCEL;
        this.cancel = new TossPaymentCancel(reason);
    }

    public void cancel() {
        if (status != PENDING_CANCEL) {
            throw new TossPaymentException(ONLY_CANCEL_PENDING_PAYMENT);
        }
        this.status = CANCELED;
    }

    public String getIdempotencyKeyForCancel() {
        return cancel.getIdempotencyKey();
    }

    public void regenerateIdempotencyKeyForCancel() {
        cancel.regenerateIdempotencyKey();
    }
}
