package moa.pay.domain;


import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class TossPaymentCancel {

    @Column(unique = true)
    private String idempotencyKey;  // 멱등키

    private String reason;

    private LocalDateTime idKeyUpdatedDate;

    public TossPaymentCancel(String reason) {
        this.reason = reason;
        regenerateIdempotencyKey();
    }

    public void regenerateIdempotencyKey() {
        this.idempotencyKey = UUID.randomUUID().toString() + LocalDateTime.now();
        this.idKeyUpdatedDate = LocalDateTime.now();
    }
}
