package moa.pay.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import moa.pay.application.command.PaymentPermitCommand;

public record PermitPaymentRequest(
        @NotBlank String paymentKey,
        @NotBlank String orderId,
        @Positive int amount
) {
    public PaymentPermitCommand toCommand(Long memberId) {
        return new PaymentPermitCommand(
                memberId,
                paymentKey,
                orderId,
                amount
        );
    }
}
