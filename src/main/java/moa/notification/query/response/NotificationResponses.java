package moa.notification.query.response;

import java.util.List;
import moa.notification.domain.Notifications;

public record NotificationResponses(
        Long id,
        String url,
        String message,
        boolean isRead
) {
    public static List<NotificationResponses> from(Notifications notifications) {
        return notifications.getNotifications()
                .stream()
                .map(it -> new NotificationResponses(
                        it.getId(),
                        it.getUrl(),
                        it.getMessage(),
                        it.isRead()
                ))
                .toList();
    }
}
