package moa.pay.domain;

import static java.util.concurrent.TimeUnit.MINUTES;
import static moa.pay.exception.TossPaymentExceptionType.NO_AUTHORITY_PAYMENT;
import static moa.pay.exception.TossPaymentExceptionType.PAYMENT_INVALID;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moa.pay.exception.TossPaymentException;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Slf4j
@Getter
@RedisHash("tossPayment")
public class TemporaryTossPayment {

    @Id
    private String orderId;
    private int amount;
    private Long memberId;

    @TimeToLive(unit = MINUTES)
    private long expiredTime = 10;

    public TemporaryTossPayment(String orderId, int amount, long memberId) {
        this.orderId = orderId;
        this.amount = amount;
        this.memberId = memberId;
    }

    public void check(String orderId, int amount, Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new TossPaymentException(NO_AUTHORITY_PAYMENT);
        }
        if (this.orderId.equals(orderId) && this.amount == amount) {
            return;
        }
        throw new TossPaymentException(PAYMENT_INVALID.withDetail("orderId:" + orderId));
    }
}
