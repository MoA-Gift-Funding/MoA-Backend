package moa.fixture;

import static moa.member.domain.OauthId.OauthProvider.KAKAO;

import java.util.UUID;
import moa.member.domain.Member;
import moa.member.domain.MemberStatus;
import moa.member.domain.OauthId;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberFixture {

    public static Member member(Long id, String name, String phone, MemberStatus status) {
        String string = UUID.randomUUID().toString();
        Member member = Member.builder()
                .oauthId(new OauthId(string, KAKAO))
                .phoneNumber(phone)
                .nickname(name)
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        ReflectionTestUtils.setField(member, "status", status);
        return member;
    }
}
