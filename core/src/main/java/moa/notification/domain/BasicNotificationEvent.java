package moa.notification.domain;

public record BasicNotificationEvent(
        Long memberId,
        String notificationUrl,
        String notificationTitle,
        String notificationMessage,
        String notificationImageUrl
) implements NotificationEvent {
}
