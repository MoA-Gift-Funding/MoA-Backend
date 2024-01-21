package moa.friend.application;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import moa.friend.application.command.MakeFromContactCommand;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.friend.domain.service.NewFriendsFinder;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final NewFriendsFinder newFriendsFinder;

    public void makeFromContact(MakeFromContactCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Map<String, String> phoneAndNameMap = command.phoneAndNameMap();
        List<Member> targets = newFriendsFinder.findNewFriendTargets(member, command.phones());
        List<Friend> newFriends = targets.stream()
                .map(it -> new Friend(member, it, phoneAndNameMap.get(it.getPhoneNumber())))
                .toList();
        friendRepository.saveAll(newFriends);
    }
}
