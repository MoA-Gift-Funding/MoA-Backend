package moa.notification.query;

import static java.lang.Boolean.TRUE;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.notification.domain.NotificationType.PARTY;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import moa.ApplicationTest;
import moa.fixture.MemberFixture;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationRepository;
import moa.notification.query.response.NotificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("알림 조회 서비스 (NotificationQueryService) 은(는)")
class NotificationQueryServiceTest {

    @Autowired
    private NotificationQueryService notificationQueryService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MemberFixture.member(null, "member1", "010-1111-1111", SIGNED_UP));
    }

    @Test
    void 모든_알림을_조회하며_읽지_않은_알림은_읽기_처리한다() {
        // given
        notificationRepository.save(new Notification("url1", "title", "message", "", PARTY, member));
        notificationRepository.save(new Notification("url2", "title", "message", "", PARTY, member));

        // when
        List<NotificationResponse> notifications = notificationQueryService.readAll(member.getId());

        // then
        assertThat(notifications)
                .hasSize(2)
                .extracting(NotificationResponse::isRead)
                .containsOnly(TRUE);
    }

    @Test
    void 읽지_않은_알림이_있는지_확인한다() {
        // given
        notificationRepository.save(new Notification("url1", "title", "message", "", PARTY, member));
        List<NotificationResponse> notifications = notificationQueryService.readAll(member.getId());
        assertThat(notificationQueryService.existsUnread(member.getId())).isFalse();

        notificationRepository.save(new Notification("url2", "title", "message", "", PARTY, member));

        // when
        boolean hasUnread = notificationQueryService.existsUnread(member.getId());

        // then
        assertThat(hasUnread).isTrue();
    }
}
