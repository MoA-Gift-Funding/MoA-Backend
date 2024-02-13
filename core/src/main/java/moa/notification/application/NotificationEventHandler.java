package moa.notification.application;

import static moa.global.config.AsyncConfig.VIRTUAL_THREAD_EXECUTOR;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import lombok.RequiredArgsConstructor;
import moa.global.fcm.FcmClient;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationEvent;
import moa.notification.domain.NotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final FcmClient client;

    @Async(VIRTUAL_THREAD_EXECUTOR)
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(value = NotificationEvent.class, phase = AFTER_COMMIT)
    public void push(NotificationEvent event) {
        System.out.println(event.toString());
        Member member = memberRepository.getById(event.memberId());
        Notification notification = new Notification(
                event.notificationUrl(),
                event.notificationTitle(),
                event.notificationMessage(),
                event.notificationImageUrl(),
                member
        );
        notificationRepository.save(notification);
        String deviceToken = member.getPhone().getDeviceToken();
        client.sendMessage(
                deviceToken,
                notification.getTitle(),
                notification.getMessage(),
                notification.getImageUrl(),
                notification.getUrl()
        );
    }
}
