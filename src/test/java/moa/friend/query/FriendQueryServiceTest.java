package moa.friend.query;

import static moa.fixture.MemberFixture.member;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.member.domain.OauthId.OauthProvider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.friend.query.response.FriendResponse;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.OauthId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("친구 조회 서비스 (FriendQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
@SpringBootTest
class FriendQueryServiceTest {

    @Autowired
    private FriendQueryService friendQueryService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Test
    void 특정_회원의_친구들_중_차단되지_않은_친구들을_조회한다() {
        // given
        Member member1 = member(null, "회원 1", "010-1111-1111", SIGNED_UP);
        Member member2 = Member.builder()
                .oauthId(new OauthId("2", KAKAO))
                .phoneNumber("010-2222-2222")
                .nickname("회원 2")
                .birthyear("2002")
                .birthday("1204")
                .profileImageUrl("profile 2")
                .build();
        Member member3 = member(null, "회원 3", "010-3333-3333", SIGNED_UP);
        memberRepository.saveAll(List.of(member1, member2, member3));

        Long friendId = friendRepository.save(new Friend(member1, member2, "바보 2")).getId();
        friendRepository.save(new Friend(member1, member3, "바보 3")).block();

        // when
        List<FriendResponse> result = friendQueryService.findUnblockedFriendsByMemberId(member1.getId());

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new FriendResponse(
                                friendId,
                                member2.getId(),
                                "profile 2",
                                "바보 2",
                                "회원 2",
                                "010-2222-2222",
                                "1204",
                                "2002"
                        )
                ));
    }

    @Test
    void 특정_회원의_친구들_중_차단된_친구들을_조회한다() {
        // given
        Member member1 = member(null, "회원 1", "010-1111-1111", SIGNED_UP);
        Member member2 = member(null, "회원 2", "010-2222-2222", SIGNED_UP);
        Member member3 = Member.builder()
                .oauthId(new OauthId("3", KAKAO))
                .phoneNumber("010-3333-3333")
                .nickname("회원 3")
                .birthyear("2002")
                .birthday("1204")
                .profileImageUrl("profile 3")
                .build();
        memberRepository.saveAll(List.of(member1, member2, member3));

        friendRepository.save(new Friend(member1, member2, "바보 2"));
        Friend blocked = friendRepository.save(new Friend(member1, member3, "바보 3"));
        blocked.block();

        // when
        List<FriendResponse> result = friendQueryService.findBlockedFriendsByMemberId(member1.getId());

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new FriendResponse(
                                blocked.getId(),
                                member3.getId(),
                                "profile 3",
                                "바보 3",
                                "회원 3",
                                "010-3333-3333",
                                "1204",
                                "2002"
                        )
                ));
    }
}
