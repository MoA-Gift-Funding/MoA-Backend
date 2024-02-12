package moa.notification.query;

import lombok.RequiredArgsConstructor;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.notification.domain.NotificationRepository;
import moa.notification.domain.Notifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public Notifications readAll(Long memberId) {
        Member member = memberRepository.getById(memberId);
        Notifications notifications = notificationRepository.findNotifications(member);
        notifications.readAll(member);
        return notifications;
    }

    public boolean existsUnread(Long memberId) {
        Member member = memberRepository.getById(memberId);
        return notificationRepository.existsByUnread(member);
    }
}
