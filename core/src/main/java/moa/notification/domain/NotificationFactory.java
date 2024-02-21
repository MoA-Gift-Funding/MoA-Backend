package moa.notification.domain;

import static moa.notification.domain.NotificationType.CHECK;
import static moa.notification.domain.NotificationType.MESSAGE;
import static moa.notification.domain.NotificationType.PARTY;

import moa.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    private static final String FUNDING_DETAIL_APP_PATH = "giftMoA://navigation?name=FundDetail&fundingId=";
    private static final String MY_FUNDING_APP_PATH = "giftMoA://navigation?name=MyFunding&fundingId=";


    /**
     * 펀딩 개설 완료 ✅ 펀딩 참여 완료 (메시지 미작성) ✅ 펀딩 참여 완료 (메시지 작성) ✅ 펀딩 달성 ✅ 펀딩 종료(미달성) ✅ - batch 펀딩 달성 하루 전 ✅- batch 펀딩 취소 펀딩 취소
     * (상품 공급 중지) ✅ - batch 생일 알림
     */
    public Notification generateFundingCreatedNotification(
            String fundingOwnerName,
            String fundingTitle,
            String productImageUrl,
            Long fundingId,
            Member target
    ) {
        return new Notification(
                FUNDING_DETAIL_APP_PATH + fundingId,
                "친구의 새로운 펀딩",
                "%s님의 [%s] 펀딩이 개설되었어요. 친구의 펀딩을 구경해볼까요? 🎁"
                        .formatted(fundingOwnerName, fundingTitle),
                productImageUrl,
                PARTY,
                target
        );
    }

    public Notification generateFundingFinishNotification(
            String fundingTitle,
            String productImageUrl,
            Long fundingId,
            Member target
    ) {
        return new Notification(
                FUNDING_DETAIL_APP_PATH + fundingId,
                "펀딩 달성",
                "[%s] 펀딩이 달성 완료됐어요. 내 펀딩에서 상품 수령 버튼을 눌러주세요 🎁"
                        .formatted(fundingTitle),
                productImageUrl,
                PARTY,
                target
        );
    }

    public Notification generateFundingStoppedNotification(
            Long fundingId,
            Member target
    ) {
        return new Notification(
                MY_FUNDING_APP_PATH + fundingId,
                "펀딩 중단",
                "입점사의 펀딩 상품 공급 중단 이슈로 펀딩이 취소되었어요🥲 마이페이지에서 입금받을 계좌 번호를 입력해주세요.",
                null,
                CHECK,
                target
        );
    }

    public Notification generateFundingExpiredNotification(
            String fundingTitle,
            String productImageUrl,
            Long fundingId,
            Member target
    ) {
        return new Notification(
                FUNDING_DETAIL_APP_PATH + fundingId,
                "펀딩 종료",
                "[%s] 펀딩이 종료됐어요🙀 펀딩 채우기를 통해 펀딩을 달성해보세요. 달성되지 못한 펀딩은 참가자들에게 전액 환불됩니다🥹"
                        .formatted(fundingTitle),
                productImageUrl,
                CHECK,
                target
        );
    }

    public Notification generateFundingSoonExpireNotification(
            String fundingTitle,
            String productImageUrl,
            Long fundingId,
            Member target
    ) {
        return new Notification(
                FUNDING_DETAIL_APP_PATH + fundingId,
                "펀딩 종료 D-1️⃣",
                "[%s] 펀딩 종료일이 하루밖에 남지 않았어요🤧 펀딩 채우기를 통해 펀딩을 달성해보세요!"
                        .formatted(fundingTitle),
                productImageUrl,
                CHECK,
                target
        );
    }

    public Notification generateFundingParticipateNotification(
            String participantName,
            String fundingMessage,
            String participantProfileUrl,
            Long fundingId,
            Long fundingMessageId,
            Member target
    ) {
        if (fundingMessage == null || fundingMessage.isBlank()) {
            return generateFundingParticipateWithoutMessageNotification(
                    participantName,
                    participantProfileUrl,
                    fundingId,
                    target
            );
        }
        return new Notification(
                FUNDING_DETAIL_APP_PATH + "&messageId=%s"
                        .formatted(fundingMessageId),
                "펀딩 메세지 도착",
                "💌 from %s %s"
                        .formatted(participantName, fundingMessage),
                participantProfileUrl,
                MESSAGE,
                target
        );
    }

    private Notification generateFundingParticipateWithoutMessageNotification(
            String fundingTitle,
            String participantProfileUrl,
            Long fundingId,
            Member target
    ) {
        return new Notification(
                FUNDING_DETAIL_APP_PATH + fundingId,
                "친구의 펀딩 참여",
                "%s 님이 내 펀딩에 참여했어요🤗"
                        .formatted(fundingTitle),
                participantProfileUrl,
                PARTY,
                target
        );
    }

    public Notification generateFundingCancelNotification(
            String fundingOwnerName,
            String fundingTitle,
            Member target
    ) {
        return new Notification(
                "giftMoA://navigation?name=MyFunding",
                "펀딩 취소",
                "%s님이 [%s] 펀딩을 취소했어요🥲 참여한 펀딩 금액은 3-5 영업일 이내로 환불될 예정입니다."
                        .formatted(fundingOwnerName, fundingTitle),
                null,
                CHECK,
                target
        );
    }
}
