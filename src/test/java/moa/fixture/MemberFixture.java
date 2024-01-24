package moa.fixture;

import static moa.member.domain.OauthId.OauthProvider.KAKAO;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.UUID;
import moa.member.domain.Member;
import moa.member.domain.MemberStatus;
import moa.member.domain.OauthId;

public class MemberFixture {

    public static Member member(Long id, String name, String phone, MemberStatus status) {
        String string = UUID.randomUUID().toString();
        Member member = new Member(
                new OauthId(string, KAKAO),
                null,
                name,
                null,
                null,
                null,
                phone
        );
        setField(member, "id", id);
        setField(member, "status", status);
        return member;
    }
}
