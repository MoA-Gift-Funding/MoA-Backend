package moa.pay;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;

public record PrepayRequest(
        @NotBlank String orderId,
        @Positive int amount
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
