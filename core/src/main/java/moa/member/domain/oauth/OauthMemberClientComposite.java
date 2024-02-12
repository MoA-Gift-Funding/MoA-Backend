package moa.member.domain.oauth;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Set;
import moa.member.domain.Member;
import moa.member.domain.OauthId.OauthProvider;
import org.springframework.stereotype.Component;

@Component
public class OauthMemberClientComposite {

    private final Map<OauthProvider, OauthMemberClient> clients;

    public OauthMemberClientComposite(Set<OauthMemberClient> clients) {
        this.clients = clients.stream()
                .collect(toMap(OauthMemberClient::supportsProvider, identity()));
    }

    public Member fetch(OauthProvider provider, String accessToken) {
        return clients.get(provider).fetch(accessToken);
    }
}
