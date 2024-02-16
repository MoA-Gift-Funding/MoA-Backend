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
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class FundingEventHandler {

    private final FriendRepository friendRepository;
    private final NotificationService notificationService;
    private final NotificationFactory notificationFactory;

    @Async(VIRTUAL_THREAD_EXECUTOR)
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(value = FundingCreateEvent.class, phase = AFTER_COMMIT)
    public void notifyCreatedFundingToFriends(FundingCreateEvent event) {
        Funding funding = event.funding();
        Member fundingOwner = funding.getMember();
        List<Friend> friendsUnblockedOwner = friendRepository.findUnblockedByTargetId(fundingOwner);
        List<Friend> ownersUnblockedFriends = friendRepository.findUnblockedByMemberId(fundingOwner);
        List<Member> unblockedFriends = getUnblockedFriends(friendsUnblockedOwner, ownersUnblockedFriends);
        List<Notification> notifications = unblockedFriends.stream()
                .map(target -> notificationFactory.generateFundingCreatedNotification(
                        getNickName(fundingOwner, friendsUnblockedOwner),
                        funding.getTitle(),
                        funding.getProduct().getImageUrl(),
                        funding.getId(),
                        target
                )).toList();
        for (Notification notification : notifications) {
            notificationService.push(notification);
        }
    }

    private List<Member> getUnblockedFriends(List<Friend> friendsUnblockedOwner, List<Friend> ownersUnblockedFriends) {
        List<Member> unblocked = friendsUnblockedOwner.stream()
                .map(Friend::getMember)
                .collect(Collectors.toList());
        List<Member> myUnblockedFriendsTarget = ownersUnblockedFriends.stream()
                .map(Friend::getTarget)
                .toList();
        unblocked.retainAll(myUnblockedFriendsTarget);
        return unblocked;
    }

    private String getNickName(Member owner, List<Friend> friendsUnblockedOwner) {
        return friendsUnblockedOwner.stream()
                .filter(friend -> friend.getTarget().equals(owner))
                .findAny()
                .map(Friend::getNickname)
                .orElseGet(owner::getNickname);
    }
}
