package moa.funding.domain;

import static moa.funding.exception.FundingExceptionType.CAN_NOT_VISIBLE_FUNDING;

import lombok.RequiredArgsConstructor;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.funding.exception.FundingException;
import moa.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FundingValidator {

    private final FriendRepository friendRepository;

    public void validateVisible(Member member, Funding funding) {
        Member otherMember = funding.getMember();
        // 친구가 아닌 사람의 펀딩을 볼 수 없다.
        Friend memberToFriend = friendRepository.findByMemberAndTarget(member, otherMember)
                .orElseThrow(() -> new FundingException(CAN_NOT_VISIBLE_FUNDING));
        // 현재 친구 관계는 항상 양방향이므로 예외가 발생할 수 없음
        Friend friendToMember = friendRepository.findByMemberAndTarget(otherMember, member)
                .orElseThrow(() -> new FundingException(CAN_NOT_VISIBLE_FUNDING));
        if (memberToFriend.isBlocked() || friendToMember.isBlocked()) {
            throw new FundingException(CAN_NOT_VISIBLE_FUNDING);
        }
    }
}
