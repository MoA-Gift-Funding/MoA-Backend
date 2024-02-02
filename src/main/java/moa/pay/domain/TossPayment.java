package moa.pay.domain;

import static lombok.AccessLevel.PROTECTED;
import static moa.pay.exception.TossPaymentExceptionType.ALREADY_USED_PAYMENT;
import static moa.pay.exception.TossPaymentExceptionType.NO_AUTHORITY_PAYMENT;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.funding.domain.Price;
import moa.pay.exception.TossPaymentException;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class TossPayment {

    @Id
    private String paymentKey;

    @Column(nullable = false, unique = true)
    private String orderId;

    private String orderName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_amount"))
    private Price totalAmount;

    @Column
    private Long memberId;

    private boolean used;

    @CreatedDate
    private LocalDateTime createdDate;

    public TossPayment(String paymentKey, String orderId, String orderName, String totalAmount, Long memberId) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.totalAmount = Price.from(totalAmount);
        this.memberId = memberId;
    }

    public void use(Long memberId) {
        if (used) {
            throw new TossPaymentException(ALREADY_USED_PAYMENT);
        }
        if (this.memberId != memberId) {
            throw new TossPaymentException(NO_AUTHORITY_PAYMENT);
        }
        this.used = true;
    }
}
