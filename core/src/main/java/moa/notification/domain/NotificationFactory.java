package moa.notification.domain;

import static moa.notification.domain.NotificationType.PARTY;

import moa.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    public Notification generateFundingCreatedNotification(
            String fundingOwnerName,
            String fundingTitle,
            String productImageUrl,
            Long fundingId,
            Member target
    ) {
        return new Notification(
                "giftMoA://navigation?name=FundDetail&fundingId=" + fundingId,
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
            Long orderId,
            Member target
    ) {
        return new Notification(
                "giftMoA://navigation?name=MyOrder&orderId=" + orderId,
                "펀딩 달성",
                "[%s] 펀딩이 달성 완료됐어요. 곧 펀딩 상품의 배송이 시작됩니다. 🎁"
                        .formatted(fundingTitle),
                productImageUrl,
                PARTY,
                target
        );
    }
}
