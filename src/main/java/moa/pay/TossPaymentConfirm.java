package moa.pay;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("tossPayment")
public record TossPaymentConfirm(
        @Id
        String orderId,
        int amount,
        long memberId,

        @TimeToLive(unit = MINUTES)
        long expiredTime
) {
    public boolean isValid(String orderId, int amount) {
        return Objects.equals(this.orderId, orderId)
                && this.amount == amount;
    }
}
