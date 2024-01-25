package moa.funding.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.friend.domain.Friend;
import moa.friend.query.FriendQueryRepository;
import moa.funding.domain.Funding;
import moa.funding.query.response.FundingDetailResponse;
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

    private final MemberQueryRepository memberQueryRepository;
    private final FundingQueryRepository fundingRepository;
    private final FriendQueryRepository friendQueryRepository;

    public Page<MyFundingDetail> findMyFundings(Long memberId, Pageable pageable) {
        return fundingRepository.findAllByMemberId(memberId, pageable)
                .map(MyFundingDetail::from);
    }

    public FundingDetailResponse findFundingById(Long memberId, Long fundingId) {
        Member member = memberQueryRepository.getById(memberId);
        Funding funding = fundingRepository.getById(fundingId);
        List<Friend> friends = friendQueryRepository.findAllByMemberId(memberId);
        return FundingDetailResponse.of(funding, member, friends);
    }

    public Page<FundingResponse> findFundings(Long memberId, Pageable pageable) {
        List<Friend> friends = friendQueryRepository.findUnblockedByMemberId(memberId);
        Page<Funding> fundings = fundingRepository.findMembersFriend(memberId, pageable);
        return fundings.map(funding -> FundingResponse.of(funding, friends));
    }
}
