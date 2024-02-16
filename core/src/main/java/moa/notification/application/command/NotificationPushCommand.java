package moa.notification.application.command;

import moa.member.domain.Member;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationType;

public record NotificationPushCommand(
        Long memberId,
        String url,
        String title,
        String message,
        String imageUrl,
        NotificationType type
) {
    public Notification toNotification(Member member) {
        return new Notification(
                url,
                title,
                message,
                imageUrl,
                type,
                member
        );
    }
}
