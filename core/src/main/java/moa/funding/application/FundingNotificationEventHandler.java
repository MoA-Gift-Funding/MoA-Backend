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
import moa.funding.domain.FundingCancelEvent;
import moa.funding.domain.FundingCreateEvent;
import moa.funding.domain.FundingParticipant;
import moa.funding.domain.FundingParticipateEvent;
import moa.funding.domain.FundingParticipateRepository;
import moa.funding.domain.FundingRepository;
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
@Async(VIRTUAL_THREAD_EXECUTOR)
@Transactional(propagation = REQUIRES_NEW)
public class FundingNotificationEventHandler {

    private final FundingRepository fundingRepository;
    private final FriendRepository friendRepository;
    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;
    private final FundingParticipateRepository fundingParticipateRepository;

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

    @TransactionalEventListener(value = FundingParticipateEvent.class, phase = AFTER_COMMIT)
    public void push(FundingParticipateEvent event) {
        Funding funding = fundingRepository.getById(event.fundingId());
        Member fundingOwner = funding.getMember();
        FundingParticipant participant = fundingParticipateRepository.getById(event.participant().getId());
        Member participantMember = participant.getMember();
        Friend friend = friendRepository.getByMemberAndTarget(fundingOwner, participantMember);
        var notification = notificationFactory.generateFundingParticipateNotification(
                friend.getNickname(),
                participant.getFundingMessage().getContent(),
                participantMember.getProfileImageUrl(),
                funding.getId(),
                participant.getFundingMessage().getId(),
                fundingOwner
        );
        notificationService.push(notification);
    }

    @TransactionalEventListener(value = FundingCancelEvent.class, phase = AFTER_COMMIT)
    public void push(FundingCancelEvent event) {
        Funding funding = fundingRepository.getById(event.fundingId());
        Member fundingOwner = funding.getMember();
        List<Friend> friendsTargetOwner = friendRepository.findAllByTargetId(fundingOwner);
        List<FundingParticipant> participants = funding.getParticipatingParticipants();
        List<Notification> notifications = participants.stream()
                .map(FundingParticipant::getMember)
                .map(target -> notificationFactory.generateFundingCancelNotification(
                        funding.getId(),
                        getNickName(target, friendsTargetOwner),
                        funding.getTitle(),
                        target
                )).toList();
        for (Notification notification : notifications) {
            notificationService.push(notification);
        }
    }

    private String getNickName(Member member, List<Friend> friendsTargetOwner) {
        return friendsTargetOwner.stream()
                .filter(friend -> friend.getMember().equals(member))
                .findAny()
                .map(Friend::getNickname)
                .orElseGet(member::getNickname);
    }
}
