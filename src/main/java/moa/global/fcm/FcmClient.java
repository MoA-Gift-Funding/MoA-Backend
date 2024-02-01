package moa.global.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmClient {

    public void sendMessage(String targetDeviceToken, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        Message message = Message.builder()
                .setToken(targetDeviceToken)
                .setNotification(notification)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.debug("알림 전송 성공 : " + response);
        } catch (Exception e) {
            log.error("FCM 알림 전송에 실패했습니다.", e);
        }
    }
}
