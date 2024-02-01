package moa.pay.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    private String totalAmount;

    @Column
    private Long memberId;

    @CreatedDate
    private LocalDateTime createdDate;

    public TossPayment(String paymentKey, String orderId, String orderName, String totalAmount, Long memberId) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.memberId = memberId;
    }
}
