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
import moa.funding.domain.FundingFinishEvent;
import moa.funding.domain.FundingMessage;
import moa.funding.domain.FundingParticipant;
import moa.funding.domain.FundingParticipateEvent;
import moa.funding.domain.FundingRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.notification.application.NotificationService;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationFactory;
import moa.product.domain.Product;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Async(VIRTUAL_THREAD_EXECUTOR)
@Transactional(propagation = REQUIRES_NEW)
public class FundingNotificationEventHandler {

    private final FundingRepository fundingRepository;
    private final FriendRepository friendRepository;
    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    @TransactionalEventListener(value = FundingCreateEvent.class, phase = AFTER_COMMIT)
    public void push(FundingCreateEvent event) {
        Funding funding = event.funding();
        Member fundingOwner = funding.getMember();
        List<Friend> friendsUnblockedOwner = friendRepository.findUnblockedByTargetId(fundingOwner);
        List<Friend> ownersUnblockedFriends = friendRepository.findUnblockedByMemberId(fundingOwner);
        List<Member> unblockedFriends = getUnblockedFriends(friendsUnblockedOwner, ownersUnblockedFriends);
        List<Notification> notifications = unblockedFriends.stream()
                .map(target -> notificationFactory.generateFundingCreatedNotification(
                        getNickName(target, friendsUnblockedOwner),
                        funding.getTitle(),
                        funding.getProduct().getImageUrl(),
                        funding.getId(),
                        target
                )).toList();
        for (Notification notification : notifications) {
            notificationService.push(notification);
        }
    }

    @TransactionalEventListener(value = FundingFinishEvent.class, phase = AFTER_COMMIT)
    public void push(FundingFinishEvent event) {
        Funding funding = fundingRepository.getById(event.fundingId());
        Product product = funding.getProduct();
        Notification notification = notificationFactory.generateFundingFinishNotification(
                funding.getTitle(),
                product.getImageUrl(),
                funding.getId(),
                funding.getMember()
        );
        notificationService.push(notification);
    }

    @TransactionalEventListener(value = FundingParticipateEvent.class, phase = AFTER_COMMIT)
    public void push(FundingParticipateEvent event) {
        Funding funding = fundingRepository.getById(event.fundingId());
        Member fundingOwner = funding.getMember();
        Member participant = memberRepository.getById(event.participantId());
        FundingMessage fundingMessage = getFundingMessage(funding, participant);
        List<Friend> ownersUnblockedFriends = friendRepository.findUnblockedByMemberId(fundingOwner);
        String participantNickName = getNickName(participant, ownersUnblockedFriends);
        var notification = notificationFactory.generateFundingParticipateNotification(
                participantNickName,
                fundingMessage == null ? null : fundingMessage.getContent(),
                participant.getProfileImageUrl(),
                funding.getId(),
                fundingMessage == null ? null : fundingMessage.getId(),
                fundingOwner
        );
        notificationService.push(notification);
    }

    private FundingMessage getFundingMessage(Funding funding, Member participant) {
        return funding.getParticipants().stream()
                .filter(it -> it.getMember().equals(participant))
                .findAny()
                .map(FundingParticipant::getFundingMessage)
                .orElse(null);
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

    private String getNickName(Member target, List<Friend> friendsUnblockedOwner) {
        return friendsUnblockedOwner.stream()
                .filter(friend -> friend.getMember().equals(target))
                .findAny()
                .map(Friend::getNickname)
                .orElseGet(target::getNickname);
    }
}
