package moa.pay.domain;


import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class TossPaymentCancel {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "toss_payment_id")
    private TossPayment tossPayment;

    @Column(unique = true)
    private String idempotencyKey;  // 멱등키

    @CreatedDate
    private LocalDateTime createdDate;

    public TossPaymentCancel(TossPayment tossPayment) {
        this.tossPayment = tossPayment;
        this.idempotencyKey = UUID.randomUUID().toString() + LocalDateTime.now();
    }
}
