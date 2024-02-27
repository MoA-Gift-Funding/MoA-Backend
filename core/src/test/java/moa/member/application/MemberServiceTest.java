package moa.member.application;

import static moa.fixture.MemberFixture.member;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.member.domain.MemberStatus.WITHDRAW;
import static moa.member.domain.OauthId.OauthProvider.KAKAO;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import moa.ApplicationTest;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.oauth.OauthMemberClientComposite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("사용자 서비스 (MemberService) 은(는)")
@DisplayNameGeneration(ReplaceUnderscores.class)
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private OauthMemberClientComposite oauthMemberClientComposite;

    @Nested
    class 회원_탈퇴_시_ {

        @Test
        void 회원_정보가_null로_초기화된다() {
            // given
            Member member = member(null, "회원 1", "010-1111-1111", SIGNED_UP);
            memberRepository.save(member);

            // when
            memberService.withdraw(member.getId(), KAKAO, "temp");

            // then
            var updated = memberRepository.getById(member.getId());
            assertSoftly(
                    softly -> {
                        softly.assertThat(updated.getOauthId()).isNull();
                        softly.assertThat(updated.getEmail()).isNull();
                        softly.assertThat(updated.getNickname()).isNull();
                        softly.assertThat(updated.getBirthyear()).isNull();
                        softly.assertThat(updated.getBirthday()).isNull();
                        softly.assertThat(updated.getProfileImageUrl()).isNull();
                        softly.assertThat(updated.getPhone()).isNull();
                        softly.assertThat(updated.getTossCustomerKey()).isNull();
                        softly.assertThat(updated.getStatus()).isEqualTo(WITHDRAW);
                    }
            );
        }
    }
}
