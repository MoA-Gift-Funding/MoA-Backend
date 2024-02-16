package moa.notification.query.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import moa.notification.domain.Notifications;

public record NotificationResponse(
        Long id,
        String url,
        String title,
        String message,
        String imageUrl,
        @Schema(description = "party, message, check") String type,
        boolean isRead
) {
    public static List<NotificationResponse> from(Notifications notifications) {
        return notifications.getNotifications()
                .stream()
                .map(it -> new NotificationResponse(
                        it.getId(),
                        it.getUrl(),
                        it.getTitle(),
                        it.getMessage(),
                        it.getImageUrl(),
                        it.getType(),
                        it.isRead()
                ))
                .toList();
    }
}
