package moa.friend.application;

import static moa.fixture.MemberFixture.member;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import moa.ApplicationTest;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.member.application.MemberService;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.oauth.OauthMemberClientComposite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@ApplicationTest
@DisplayName("친구 이벤트 핸들러 (FriendEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FriendEventHandlerTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private EntityManager entityManager;

    @MockBean
    private OauthMemberClientComposite oauthMemberClientComposite;

    @Test
    void 회원_탈퇴_이벤트를_받아_해당_회원의_친구관계를_모두_제거한다() {
        // given
        Member member1 = member(null, "회원 1", "010-1111-1111", SIGNED_UP);
        Member member2 = member(null, "회원 2", "010-2222-2222", SIGNED_UP);
        Member member3 = member(null, "회원 3", "010-3333-3333", SIGNED_UP);
        memberRepository.saveAll(List.of(member1, member2, member3));
        friendRepository.saveAll(List.of(
                new Friend(member1, member2, "1 - 2"),
                new Friend(member2, member1, "2 - 1"),
                new Friend(member1, member3, "1 - 3"),
                new Friend(member3, member1, "3 - 1"),

                new Friend(member2, member3, "2 - 3"),
                new Friend(member3, member2, "3 - 2")
        ));
        entityManager.clear();

        // when
        memberService.withdraw(member1.getId(), "token");

        // then
        assertThat(friendRepository.findAll())
                .hasSize(2)
                .extracting(Friend::getNickname)
                .containsExactly("2 - 3", "3 - 2");
    }
}
