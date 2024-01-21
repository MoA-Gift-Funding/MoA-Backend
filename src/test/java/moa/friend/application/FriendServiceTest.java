package moa.friend.application;

import static moa.member.domain.MemberStatus.PRESIGNED_UP;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.member.domain.OauthId.OauthProvider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import moa.friend.application.command.MakeFromContactCommand;
import moa.friend.application.command.MakeFromContactCommand.ContactInfo;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.OauthId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("친구 서비스 (FriendService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class FriendServiceTest {

    @Autowired
    private FriendService friendService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private EntityManager entityManager;


    @Nested
    class 연락처로부터_친구_추가_시 {

        @Test
        void 연락처_중_최종_가입을_완료했으며_기존에_친구가_아니었던_사람들과만_친구가_된다() {
            // given
            Member member1 = Member.builder()
                    .oauthId(new OauthId("1", KAKAO))
                    .phoneNumber("010-1111-1111")
                    .build();
            Member member2 = Member.builder()
                    .oauthId(new OauthId("2", KAKAO))
                    .phoneNumber("010-2222-2222")
                    .build();
            Member member3 = Member.builder()
                    .oauthId(new OauthId("3", KAKAO))
                    .phoneNumber("010-3333-3333")
                    .build();
            Member member4 = Member.builder()
                    .oauthId(new OauthId("4", KAKAO))
                    .phoneNumber("010-4444-4444")
                    .build();
            Member member5 = Member.builder()
                    .oauthId(new OauthId("5", KAKAO))
                    .phoneNumber("010-5555-5555")
                    .build();
            ReflectionTestUtils.setField(member1, "status", SIGNED_UP);
            ReflectionTestUtils.setField(member2, "status", SIGNED_UP);
            ReflectionTestUtils.setField(member3, "status", PRESIGNED_UP);
            ReflectionTestUtils.setField(member4, "status", SIGNED_UP);
            ReflectionTestUtils.setField(member5, "status", SIGNED_UP);
            memberRepository.saveAll(List.of(member1, member2, member3, member4, member5));
            friendRepository.saveAll(List.of(
                    new Friend(member1, member2, "기존 친구 2")
            ));
            entityManager.clear();

            // when
            friendService.makeFromContact(new MakeFromContactCommand(
                    member1.getId(),
                    List.of(
                            new ContactInfo("친구 2", "010-2222-2222"),
                            new ContactInfo("친구 3", "010-3333-3333"),
                            new ContactInfo("친구 4", "010-4444-4444"),
                            new ContactInfo("친구 5", "010-5555-5555"),
                            new ContactInfo("친구 6", "010-6666-6666")
                    )
            ));

            // then
            assertThat(friendRepository.findAll())
                    .extracting(Friend::getNickname)
                    .containsExactly(
                            "기존 친구 2",
                            "친구 4",
                            "친구 5"
                    );
        }
    }
}
