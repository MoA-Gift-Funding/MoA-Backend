package moa.pay.application.command;

import moa.pay.domain.TemporaryTossPayment;

public record TempPaymentSaveCommand(
        Long memberId,
        String orderId,
        int amount
) {
    public TemporaryTossPayment toTemporaryPayment() {
        return new TemporaryTossPayment(
                orderId,
                amount,
                memberId
        );
    }
}
