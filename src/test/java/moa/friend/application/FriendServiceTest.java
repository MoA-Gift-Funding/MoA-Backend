package moa.friend.application;

import static moa.member.MemberFixture.member;
import static moa.member.domain.MemberStatus.PRESIGNED_UP;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import moa.friend.application.command.MakeFromContactCommand;
import moa.friend.application.command.MakeFromContactCommand.ContactInfo;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        void 연락처_중_최종_가입을_완료했으며_기존에_친구가_아니었던_사람들과만_추가로_친구가_된다() {
            // given
            Member member1 = member(null, "회원 1", "010-1111-1111", SIGNED_UP);
            Member member2 = member(null, "회원 2", "010-2222-2222", SIGNED_UP);
            Member member3 = member(null, "회원 3", "010-3333-3333", SIGNED_UP);
            Member member4 = member(null, "회원 4", "010-4444-4444", SIGNED_UP);
            Member member5 = member(null, "회원 5", "010-5555-5555", PRESIGNED_UP);
            memberRepository.saveAll(List.of(member1, member2, member3, member4, member5));
            friendRepository.saveAll(List.of(
                    new Friend(member1, member2, "기존 친구 2"),
                    new Friend(member2, member1, "기존 친구 1")
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
                    .hasSize(6)
                    .extracting(Friend::getNickname)
                    .containsExactly(
                            "기존 친구 2",
                            "기존 친구 1",
                            "친구 3",
                            "회원 1",
                            "친구 4",
                            "회원 1"
                    );
        }
    }
}
