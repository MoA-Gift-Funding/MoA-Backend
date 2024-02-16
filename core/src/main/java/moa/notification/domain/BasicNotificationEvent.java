package moa.notification.domain;

public record BasicNotificationEvent(
        Long memberId,
        String notificationUrl,
        String notificationTitle,
        String notificationMessage,
        String notificationImageUrl,
        NotificationType notificationType
) implements NotificationEvent {
}
