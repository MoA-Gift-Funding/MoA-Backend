package moa.funding.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.friend.domain.Friend;
import moa.friend.query.FriendQueryRepository;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingMessage;
import moa.funding.domain.FundingStatus;
import moa.funding.domain.FundingValidator;
import moa.funding.query.response.FundingDetailResponse;
import moa.funding.query.response.FundingMessageResponse;
import moa.funding.query.response.FundingResponse;
import moa.funding.query.response.MyFundingsResponse.MyFundingDetail;
import moa.funding.query.response.ParticipatedFundingResponse;
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

    private final FundingValidator fundingValidator;
    private final MemberQueryRepository memberQueryRepository;
    private final FriendQueryRepository friendQueryRepository;
    private final FundingQueryRepository fundingQueryRepository;
    private final FundingMessageQueryRepository fundingMessageQueryRepository;
    private final FundingParticipantQueryRepository fundingParticipantQueryRepository;

    public Page<MyFundingDetail> findMyFundings(Long memberId, Pageable pageable) {
        return fundingQueryRepository.findAllByMemberId(memberId, pageable)
                .map(MyFundingDetail::from);
    }

    public FundingDetailResponse findFundingById(Long memberId, Long fundingId) {
        Member member = memberQueryRepository.getById(memberId);
        Funding funding = fundingQueryRepository.getById(fundingId);
        fundingValidator.validateVisible(member, funding);
        List<Friend> friends = friendQueryRepository.findAllByMemberId(memberId);
        return FundingDetailResponse.of(funding, member, friends);
    }

    public Page<FundingResponse> findFundings(Long memberId, List<FundingStatus> statuses, Pageable pageable) {
        List<Friend> myUnblockedFriends = friendQueryRepository.findUnblockedByMemberId(memberId);
        Page<Funding> fundings = fundingQueryRepository.findByMembersFriend(memberId, statuses, pageable);
        return fundings.map(funding -> FundingResponse.of(funding, myUnblockedFriends));
    }

    public Page<ParticipatedFundingResponse> findParticipatedFundings(Long memberId, Pageable pageable) {
        List<Friend> myFriends = friendQueryRepository.findAllByMemberId(memberId);
        return fundingParticipantQueryRepository.findAllByMemberId(memberId, pageable)
                .map(it -> ParticipatedFundingResponse.of(it, myFriends));
    }

    public Page<FundingMessageResponse> findMessages(Long memberId, Pageable pageable) {
        Page<FundingMessage> messages = fundingMessageQueryRepository.findAllByReceiverId(memberId, pageable);
        List<Friend> friends = friendQueryRepository.findAllByMemberId(memberId);
        return messages.map(message -> FundingMessageResponse.of(message, friends));
    }
}
