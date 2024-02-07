package moa.notification.query.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CheckExistsUnreadNotificationResponse(
        @Schema(description = "읽지 않은 알림이 있는지 확인한다.", example = "true") boolean hasUnread
) {
}
