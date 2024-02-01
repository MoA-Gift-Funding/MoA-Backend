package moa.pay;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PrepayRequest(
        @NotBlank String orderId,
        @Positive int amount
) {
}