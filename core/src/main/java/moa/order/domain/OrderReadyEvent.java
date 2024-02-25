package moa.order.domain;

public record OrderReadyEvent(
        Order order
) {
}
