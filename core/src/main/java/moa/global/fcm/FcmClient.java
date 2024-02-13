package moa.global.fcm;

import static moa.global.config.async.AsyncConfig.VIRTUAL_THREAD_EXECUTOR;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmClient {

    @Async(VIRTUAL_THREAD_EXECUTOR)
    public void sendMessage(
            String targetDeviceToken,
            String title,
            String content,
            String imageUrl,
            String url
    ) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .setImage(imageUrl)
                .build();
        Message message = Message.builder()
                .setToken(targetDeviceToken)
                .setNotification(notification)
                .putData("url", url)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("FCM 알림 전송 성공 : " + response);
        } catch (Exception e) {
            log.error("FCM 알림 전송에 실패했습니다.", e);
        }
    }
}
