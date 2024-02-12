package moa.friend.application;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import moa.friend.application.command.MakeFromContactCommand;
import moa.friend.application.command.UpdateFriendCommand;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.friend.domain.service.NewFriendsFinder;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final NewFriendsFinder newFriendsFinder;

    public void makeFromContact(MakeFromContactCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Map<String, String> phoneAndNameMap = command.phoneAndNameMap();
        List<Member> targets = newFriendsFinder.findNewFriendTargets(member, command.phones());
        List<Friend> newFriends = targets.stream()
                .flatMap(it -> Stream.of(
                        new Friend(member, it, phoneAndNameMap.get(it.getPhoneNumber())),
                        new Friend(it, member, member.getNickname())
                )).toList();
        friendRepository.saveAll(newFriends);
    }

    public void update(UpdateFriendCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Friend friend = friendRepository.getById(command.friendId());
        friend.validateAuthority(member);
        friend.update(command.nickname());
    }

    public void block(Long memberId, Long friendId) {
        Member member = memberRepository.getById(memberId);
        Friend friend = friendRepository.getById(friendId);
        friend.validateAuthority(member);
        friend.block();
    }

    public void unblock(Long memberId, Long friendId) {
        Member member = memberRepository.getById(memberId);
        Friend friend = friendRepository.getById(friendId);
        friend.validateAuthority(member);
        friend.unblock();
    }
}
