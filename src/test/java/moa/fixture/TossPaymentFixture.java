package moa.fixture;

import java.util.UUID;
import moa.pay.domain.TossPayment;

public class TossPaymentFixture {

    public static TossPayment tossPayment(String amount, Long memberId) {
        String string = UUID.randomUUID().toString();
        return new TossPayment(
                string,
                string,
                string,
                amount,
                memberId
        );
    }
}
