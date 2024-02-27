package moa.member.application;

import lombok.RequiredArgsConstructor;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.MemberValidator;
import moa.member.domain.OauthId.OauthProvider;
import moa.member.domain.oauth.OauthMemberClientComposite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final TransactionTemplate transactionTemplate;
    private final MemberRepository memberRepository;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final MemberValidator memberValidator;

    public Long login(OauthProvider provider, String accessToken, String refreshToken) {
        Member member = oauthMemberClientComposite.fetch(provider, accessToken);
        member.setRefreshToken(refreshToken);
        return memberRepository.findByOauthId(member.getOauthId())
                .orElseGet(() -> preSignup(member))
                .getId();
    }

    private Member preSignup(Member member) {
        return transactionTemplate.execute(status -> {
                    Member saved = memberRepository.save(member);
                    saved.preSignup(memberValidator);
                    return saved;
                }
        );
    }

    public void withdraw(Member member) {
        oauthMemberClientComposite.withdraw(member);
    }
}
