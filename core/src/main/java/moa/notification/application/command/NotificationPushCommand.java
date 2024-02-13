package moa.notification.application.command;

import moa.member.domain.Member;
import moa.notification.domain.Notification;

public record NotificationPushCommand(
        Long memberId,
        String url,
        String title,
        String message,
        String imageUrl
) {
    public Notification toNotification(Member member) {
        return new Notification(
                url,
                title,
                message,
                imageUrl,
                member
        );
    }
}
