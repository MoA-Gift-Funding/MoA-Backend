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
import moa.support.ApplicationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("친구 조회 서비스 (FriendQueryService) 은(는)")
class FriendQueryServiceTest {

    @Autowired
    private FriendQueryService friendQueryService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Test
    void 특정_회원의_친구들을_조회한다() {
        // given
        Member member1 = member(null, "회원 1", "010-1111-1111", SIGNED_UP);
        Member member2 = member(null, "회원 2", "010-2222-2222", SIGNED_UP);
        Member member3 = new Member(
                new OauthId("3", KAKAO),
                null,
                "회원 3",
                "2002",
                "1204",
                "profile 3",
                "010-3333-3333"
        );
        memberRepository.saveAll(List.of(member1, member2, member3));

        Friend unblock = friendRepository.save(new Friend(member1, member2, "바보 2"));
        Friend block = new Friend(member1, member3, "바보 3");
        block.block();
        friendRepository.save(block);

        // when
        List<FriendResponse> result = friendQueryService.findFriendsByMemberId(member1.getId());

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new FriendResponse(
                                unblock.getId(),
                                member2.getId(),
                                null,
                                "바보 2",
                                "회원 2",
                                "010-2222-2222",
                                null,
                                null,
                                false
                        ),
                        new FriendResponse(
                                block.getId(),
                                member3.getId(),
                                "profile 3",
                                "바보 3",
                                "회원 3",
                                "010-3333-3333",
                                "1204",
                                "2002",
                                true
                        )
                ));
    }
}
