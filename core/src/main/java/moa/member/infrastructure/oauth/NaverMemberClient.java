package moa.member.infrastructure.oauth;

import static moa.member.domain.OauthId.OauthProvider.NAVER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.client.oauth.naver.NaverClient;
import moa.client.oauth.naver.response.NaverMemberResponse.Response;
import moa.member.domain.Member;
import moa.member.domain.OauthId;
import moa.member.domain.OauthId.OauthProvider;
import moa.member.domain.oauth.OauthMemberClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverMemberClient implements OauthMemberClient {

    private final NaverClient naverClient;

    @Override
    public OauthProvider supportsProvider() {
        return NAVER;
    }

    @Override
    public Member fetch(String accessToken) {
        Response response = naverClient.fetchMember(accessToken)
                .response();
        return new Member(
                new OauthId(String.valueOf(response.id()), NAVER),
                response.email(),
                response.nickname(),
                response.birthyear(),
                response.birthday().replace("-", ""),
                response.profileImage(),
                response.mobile()
        );
    }
}
