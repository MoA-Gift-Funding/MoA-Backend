package moa.client.fcm;

import static com.google.firebase.messaging.AndroidConfig.Priority.HIGH;
import static moa.global.config.async.AsyncConfig.VIRTUAL_THREAD_EXECUTOR;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmClient {

    @Async(VIRTUAL_THREAD_EXECUTOR)
    public void sendMessage(
            @Nullable String targetDeviceToken,
            String title,
            String content,
            String imageUrl,
            String url,
            String type
    ) {
        log.info("call FcmClient sendMessage: title: {}, content: {}", title, content);
        if (targetDeviceToken == null) {
            return;
        }
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .setImage(imageUrl)
                .build();
        Message message = Message.builder()
                .setToken(targetDeviceToken)
                .setNotification(notification)
                .putData("url", url)
                .putData("type", type)
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(HIGH)
                        .build()
                ).build();
        try {
            String result = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 알림 전송 성공 : " + result);
        } catch (Exception e) {
            log.info("FCM 알림 전송 실패", e);
        }
    }
}
