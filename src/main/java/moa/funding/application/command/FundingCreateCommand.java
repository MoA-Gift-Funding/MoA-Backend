package moa.funding.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import moa.funding.domain.Address;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingStatus;
import moa.funding.domain.Visibility;
import moa.member.domain.Member;
import moa.product.domain.Product;

public record FundingCreateCommand(
        Long memberId,
        Long productId,
        String title,
        String description,
        LocalDate endDate,
        BigDecimal maximumPrice,
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
                Funding.MINIMUM_PRICE,
                deliveryAddress,
                visible,
                status,
                member,
                product
        );
    }
}
