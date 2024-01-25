package moa.funding.application.command;

import java.time.LocalDate;
import moa.address.domain.DeliveryAddress;
import moa.funding.domain.Funding;
import moa.funding.domain.Price;
import moa.funding.domain.Visibility;
import moa.member.domain.Member;
import moa.product.domain.Product;

public record FundingCreateCommand(
        Long memberId,
        String title,
        String description,
        LocalDate endDate,
        Visibility visible,
        Price maximumAmount,
        Long productId,
        Long deliveryAddressId,
        String deliveryRequest
) {
    public Funding toFunding(Member member, Product product, DeliveryAddress deliveryAddress) {
        return new Funding(
                title,
                description,
                endDate,
                visible,
                maximumAmount,
                member,
                product,
                deliveryAddress,
                deliveryRequest
        );
    }
}
