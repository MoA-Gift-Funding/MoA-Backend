package moa.notification.domain;

import static moa.notification.domain.NotificationType.CHECK;
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
            Long fundingId,
            Member target
    ) {
        return new Notification(
                "giftMoA://navigation?name=FundDetail&fundingId=" + fundingId,
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
                "giftMoA://navigation?name=MyFunding&fundingId=" + fundingId,
                "펀딩 중단",
                "입점사의 펀딩 상품 공급 중단 이슈로 펀딩이 취소되었어요🥲 마이페이지에서 입금받을 계좌 번호를 입력해주세요.",
                null,
                CHECK,
                target
        );
    }
}
