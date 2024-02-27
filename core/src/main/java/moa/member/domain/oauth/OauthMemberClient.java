package moa.member.domain.oauth;


import moa.member.domain.Member;
import moa.member.domain.OauthId.OauthProvider;

public interface OauthMemberClient {

    OauthProvider supportsProvider();

    Member fetch(String accessToken);

    void withdraw(String accessToken);
}
