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
import org.springframework.data.annotation.LastModifiedDate;

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

    // TODO 멱등키 유효기간이 15일임, 15일 지나면 자동으로 멱등키 재생성하는 배치 작업 추가해야 함.
    @Column(unique = true)
    private String idempotencyKey;  // 멱등키

    private String reason;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    public TossPaymentCancel(TossPayment tossPayment, String reason) {
        this.tossPayment = tossPayment;
        this.reason = reason;
        this.idempotencyKey = UUID.randomUUID().toString() + LocalDateTime.now();
    }

    public String getPaymentKey() {
        return tossPayment.getPaymentKey();
    }
}
