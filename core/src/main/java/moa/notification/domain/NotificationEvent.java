package moa.notification.domain;

public interface NotificationEvent {

    Long memberId();

    String notificationUrl();

    String notificationTitle();

    String notificationMessage();

    String notificationImageUrl();
    
    NotificationType notificationType();
}
