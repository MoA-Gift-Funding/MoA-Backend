package moa.member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.funding.application.FundingService;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.member.domain.MemberRepository;
import moa.member.domain.OauthId.OauthProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberFacade {

    private final OauthService oauthService;
    private final MemberService memberService;
    private final FundingService fundingService;
    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;

    public void withdraw(Long memberId, OauthProvider oauthProvider, String accessToken) {
        List<Funding> fundings = fundingRepository.findAllByMemberId(memberId);
        for (Funding funding : fundings) {
            fundingService.cancel(funding.getId(), memberId);
        }
        oauthService.withdraw(oauthProvider, accessToken);
        memberService.withdraw(memberId);
    }
}
