package moa.notification;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.auth.Auth;
import moa.notification.query.NotificationQueryService;
import moa.notification.query.response.CheckExistsUnreadNotificationResponse;
import moa.notification.query.response.NotificationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController implements NotificationApi {

    private final NotificationQueryService notificationQueryService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> readAll(
            @Auth(permit = SIGNED_UP) Long memberId
    ) {
        var notifications = notificationQueryService.readAll(memberId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/check")
    public ResponseEntity<CheckExistsUnreadNotificationResponse> existsUnread(
            @Auth(permit = SIGNED_UP) Long memberId
    ) {
        boolean hasUnread = notificationQueryService.existsUnread(memberId);
        return ResponseEntity.ok(new CheckExistsUnreadNotificationResponse(hasUnread));
    }
}
