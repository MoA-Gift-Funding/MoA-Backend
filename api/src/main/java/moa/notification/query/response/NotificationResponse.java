package moa.notification.query.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import moa.notification.domain.Notifications;

public record NotificationResponse(
        Long id,
        String url,
        String title,
        String message,
        String imageUrl,

        @Schema(description = "party, message, check")
        String type,

        @Schema(description = "생성일자", example = "2024-01-13 12:00:34")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdDate,

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
                        it.getCreatedDate(),
                        it.isRead()
                ))
                .toList();
    }
}
