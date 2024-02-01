package moa.pay;

import static java.util.concurrent.TimeUnit.MINUTES;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("tossPayment")
public class OrderDetail {

    @Id
    String orderId;

    int amount;
    long memberId;

    @TimeToLive(unit = MINUTES)
    long expiredTime;

    public OrderDetail(String orderId, int amount, long memberId, long expiredTime) {
        this.orderId = orderId;
        this.amount = amount;
        this.memberId = memberId;
        this.expiredTime = expiredTime;
    }
}
