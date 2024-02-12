package moa.funding.application.command;

import java.time.LocalDate;
import moa.address.domain.DeliveryAddress;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingVisibility;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.product.domain.Product;

public record FundingCreateCommand(
        Long memberId,
        String imageUrl,
        String title,
        String description,
        LocalDate endDate,
        FundingVisibility visible,
        Price maximumAmount,
        Long productId,
        Long deliveryAddressId,
        String deliveryRequestMessage
) {
    public Funding toFunding(Member member, Product product, DeliveryAddress deliveryAddress) {
        return new Funding(
                imageUrl,
                title,
                description,
                endDate,
                visible,
                maximumAmount,
                member,
                product,
                deliveryAddress,
                deliveryRequestMessage
        );
    }
}
