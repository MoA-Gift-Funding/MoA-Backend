package moa.funding.query;

import lombok.RequiredArgsConstructor;
import moa.funding.domain.Funding;
import moa.funding.query.response.FundingResponse;
import moa.funding.query.response.MyFundingsResponse.MyFundingDetail;
import moa.member.domain.Member;
import moa.member.query.MemberQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FundingQueryService {

    private final FundingQueryRepository fundingRepository;
    private final MemberQueryRepository memberRepository;

    public Page<MyFundingDetail> findMyFundings(Long memberId, Pageable pageable) {
        return fundingRepository.findAllByMemberId(memberId, pageable)
                .map(MyFundingDetail::from);
    }

    public FundingResponse findFundingById(Long memberId, Long fundingId) {
        Member member = memberRepository.getById(memberId);
        Funding funding = fundingRepository.getById(fundingId);
        return FundingResponse.from(funding, member);
    }
}
