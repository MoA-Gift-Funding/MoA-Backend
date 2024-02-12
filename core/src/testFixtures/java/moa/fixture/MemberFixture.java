package moa.fixture;

import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.UUID;
import moa.member.domain.Member;
import moa.member.domain.MemberStatus;
import moa.member.domain.OauthId;
import moa.member.domain.OauthId.OauthProvider;

public class MemberFixture {

    public static Member member(Long id, String name, String phone, MemberStatus status) {
        String string = UUID.randomUUID().toString();
        Member member = new Member(
                new OauthId(string, OauthProvider.KAKAO),
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
