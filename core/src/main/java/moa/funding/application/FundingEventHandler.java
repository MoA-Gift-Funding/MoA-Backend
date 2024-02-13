package moa.funding.application;

import static moa.global.config.async.AsyncConfig.VIRTUAL_THREAD_EXECUTOR;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import moa.friend.domain.Friend;
import moa.friend.domain.FriendRepository;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingCreateEvent;
import moa.member.domain.Member;
import moa.notification.application.NotificationService;
import moa.notification.application.command.NotificationPushCommand;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class FundingEventHandler {

    private final FriendRepository friendRepository;
    private final NotificationService notificationService;

    @Async(VIRTUAL_THREAD_EXECUTOR)
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(value = FundingCreateEvent.class, phase = AFTER_COMMIT)
    public void notifyCreatedFundingToFriends(FundingCreateEvent event) {
        Funding funding = event.funding();
        Member member = funding.getMember();
        List<Friend> friendsUnblockedMe = friendRepository.findUnblockedByTargetId(member);
        List<Friend> myUnblockedFriends = friendRepository.findUnblockedByMemberId(member);
        List<Member> unblockedFriends = getUnblockedFriends(friendsUnblockedMe, myUnblockedFriends);
        List<NotificationPushCommand> notificationPushCommands = unblockedFriends.stream()
                .map(it -> makeNotificationCreateCommand(
                        member,
                        it,
                        funding,
                        friendsUnblockedMe
                ))
                .toList();
        for (NotificationPushCommand command : notificationPushCommands) {
            notificationService.push(command);
        }
    }

    private List<Member> getUnblockedFriends(List<Friend> friendsUnblockedMe, List<Friend> myUnblockedFriends) {
        List<Member> unblocked = friendsUnblockedMe.stream()
                .map(Friend::getMember)
                .collect(Collectors.toList());
        List<Member> myUnblockedFriendsTarget = myUnblockedFriends.stream()
                .map(Friend::getTarget)
                .toList();
        unblocked.retainAll(myUnblockedFriendsTarget);
        return unblocked;
    }

    private NotificationPushCommand makeNotificationCreateCommand(
            Member fundingOwner,
            Member target,
            Funding funding,
            List<Friend> friendsUnblockedOwner
    ) {
        return new NotificationPushCommand(
                target.getId(),
                "giftMoA://navigation?name=FundDetail&fundingId=" + funding.getId(),
                "친구의 새로운 펀딩",
                "%s님의 [%s] 펀딩이 개설되었어요. 친구의 펀딩을 구경해볼까요? 🎁".formatted(
                        getNickName(fundingOwner, friendsUnblockedOwner), funding.getTitle()
                ),
                funding.getProduct().getImageUrl()
        );
    }

    private String getNickName(Member owner, List<Friend> friendsUnblockedOwner) {
        return friendsUnblockedOwner.stream()
                .filter(friend -> friend.getTarget().equals(owner))
                .findAny()
                .map(Friend::getNickname)
                .orElseGet(owner::getNickname);
    }
}
