package moa.friend.domain.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewFriendsFinder {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    public List<Member> findNewFriendTargets(Member member, List<String> phoneNumbers) {
        List<Member> candidates = memberRepository.findSignedUpByPhoneNumberIn(phoneNumbers);
        List<Member> alreadyFriends = friendRepository.findByMemberAndTargetIn(member, candidates)
                .stream()
                .map(Friend::getTarget)
                .toList();
        candidates.removeAll(alreadyFriends);
        return candidates;
    }
}
