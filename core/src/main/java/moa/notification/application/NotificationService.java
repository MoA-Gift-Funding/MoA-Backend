package moa.notification.application;

import lombok.RequiredArgsConstructor;
import moa.global.fcm.FcmClient;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.notification.application.command.NotificationPushCommand;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final FcmClient client;

    public void push(NotificationPushCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Notification notification = command.toNotification(member);
        notificationRepository.save(notification);
        String deviceToken = member.getPhone().getDeviceToken();
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
