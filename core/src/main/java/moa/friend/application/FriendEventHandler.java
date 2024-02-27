package moa.friend.application;

import lombok.RequiredArgsConstructor;
import moa.friend.domain.FriendRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.member.domain.MemberWithdrawnEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendEventHandler {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    @EventListener(MemberWithdrawnEvent.class)
    public void deleteFriends(MemberWithdrawnEvent event) {
        Member member = memberRepository.getById(event.memberId());
        friendRepository.deleteAllByMemberId(member);
    }
}
