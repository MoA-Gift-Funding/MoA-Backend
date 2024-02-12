package moa.notification.domain;

public interface NotificationEvent {

    Long memberId();

    String url();

    String message();
}
