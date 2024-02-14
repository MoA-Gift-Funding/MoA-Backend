package moa.pay.domain;


import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class TossPaymentCancel {

    // TODO 멱등키 유효기간이 15일임, 15일 지나면 자동으로 멱등키 재생성하는 배치 작업 추가해야 함.
    @Column(unique = true)
    private String idempotencyKey;  // 멱등키

    private String reason;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    public TossPaymentCancel(String reason) {
        this.reason = reason;
        this.idempotencyKey = UUID.randomUUID().toString() + LocalDateTime.now();
    }
}
