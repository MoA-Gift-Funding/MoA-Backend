package moa.friend.query;

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
import org.springframework.test.util.ReflectionTestUtils;
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
    void 특정_회원의_친구_목록을_조회한다() {
        // given
        Member member1 = Member.builder()
                .oauthId(new OauthId("1", KAKAO))
                .phoneNumber("010-1111-1111")
                .nickname("1번")
                .profileImageUrl("1번사진")
                .birthyear("2000")
                .birthday("1004")
                .build();
        Member member2 = Member.builder()
                .oauthId(new OauthId("2", KAKAO))
                .phoneNumber("010-2222-2222")
                .nickname("2번")
                .birthyear("2002")
                .birthday("1204")
                .build();
        ReflectionTestUtils.setField(member1, "status", SIGNED_UP);
        ReflectionTestUtils.setField(member2, "status", SIGNED_UP);
        memberRepository.saveAll(List.of(member1, member2));
        friendRepository.saveAll(List.of(
                new Friend(member1, member2, "바보 2번")
        ));

        // when
        List<FriendResponse> result = friendQueryService.findFriendsByMemberId(member1.getId());

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(
                        new FriendResponse(
                                null,
                                member2.getId(),
                                null,
                                "바보 2번",
                                "2번",
                                "010-2222-2222",
                                "1204",
                                "2002"
                        )
                ));
    }
}
