package moa.order.application.command;

public record OrderPlaceCommand(
        Long memberId,
        Long fundingId
) {
}
