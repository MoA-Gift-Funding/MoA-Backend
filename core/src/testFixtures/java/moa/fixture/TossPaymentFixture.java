package moa.fixture;

import static moa.pay.domain.TossPaymentStatus.PENDING_CANCEL;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.UUID;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentStatus;

public class TossPaymentFixture {

    public static TossPayment tossPayment(String amount, Long memberId, TossPaymentStatus status) {
        TossPayment tossPayment = tossPayment(amount, memberId);
        if (status == PENDING_CANCEL) {
            tossPayment.pendingCancel("reason");
        }
        setField(tossPayment, "status", status);
        return tossPayment;
    }

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
