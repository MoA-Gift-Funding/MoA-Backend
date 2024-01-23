package moa.funding.application.command;

import java.time.LocalDate;
import moa.funding.domain.Address;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingStatus;
import moa.funding.domain.Price;
import moa.funding.domain.Visibility;
import moa.member.domain.Member;
import moa.product.domain.Product;

public record FundingCreateCommand(
        Long memberId,
        Long productId,
        String title,
        String description,
        LocalDate endDate,
        Price maximumPrice,
        Address deliveryAddress,
        Visibility visible,
        FundingStatus status
) {

    public Funding toFunding(Member member, Product product) {
        return new Funding(
                title,
                description,
                endDate,
                maximumPrice,
                deliveryAddress,
                visible,
                status,
                member,
                product
        );
    }
}
