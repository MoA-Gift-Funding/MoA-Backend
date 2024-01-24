package moa.acceptance;

import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.member.domain.OauthId.OauthProvider.KAKAO;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.restassured.RestAssured;
import java.util.List;
import java.util.UUID;
import moa.friend.domain.FriendRepository;
import moa.global.jwt.JwtService;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.OauthId;
import moa.support.DataClearExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(DataClearExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DisplayNameGeneration(ReplaceUnderscores.class)
public abstract class AcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    protected void setUp() {
        RestAssured.port = port;
    }

    protected Member signup(String name, String phone) {
        Member member = new Member(
                new OauthId(UUID.randomUUID().toString(), KAKAO),
                null,
                name,
                null,
                null,
                null,
                phone
        );
        ReflectionTestUtils.setField(member, "status", SIGNED_UP);
        return memberRepository.save(member);
    }

    protected String login(Member member) {
        return jwtService.createAccessToken(member.getId());
    }

    protected Long getFriendId(Member me, Member target) {
        return friendRepository.findByMemberAndTargetIn(me, List.of(target))
                .get(0)
                .getId();
    }
}
