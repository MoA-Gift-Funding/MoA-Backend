package moa.notification.application;

import lombok.RequiredArgsConstructor;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationRepository;
import moa.notification.fcm.FcmClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmClient client;

    public void push(Notification notification) {
        notificationRepository.save(notification);
        String deviceToken = notification.getMember().getPhone().getDeviceToken();
        client.sendMessage(
                deviceToken,
                notification.getTitle(),
                notification.getMessage(),
                notification.getImageUrl(),
                notification.getUrl(),
                notification.getType()
        );
    }
}
