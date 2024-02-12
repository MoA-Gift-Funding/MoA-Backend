package moa.notification.query;

import lombok.RequiredArgsConstructor;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationEvent;
import moa.notification.domain.NotificationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @EventListener(NotificationEvent.class)
    public void save(NotificationEvent event) {
        Member member = memberRepository.getById(event.memberId());
        Notification notification = new Notification(event.notificationUrl(), event.notificationMessage(), member);
        notificationRepository.save(notification);
    }
}
