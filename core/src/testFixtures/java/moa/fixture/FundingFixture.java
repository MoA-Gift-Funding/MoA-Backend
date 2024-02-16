package moa.fixture;

import java.time.LocalDate;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingVisibility;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.product.domain.Product;

public class FundingFixture {

    public static Funding funding(
            Member owner,
            Product product,
            String maximumAmount
    ) {
        return funding(owner, product, maximumAmount, LocalDate.now().plusWeeks(4));
    }

    public static Funding funding(
            Member owner,
            Product product,
            String maximumAmount,
            LocalDate endDate
    ) {
        Funding funding = new Funding(
                null,
                "",
                "",
                endDate,
                FundingVisibility.PUBLIC,
                Price.from(maximumAmount),
                owner,
                product,
                null,
                ""
        );
        return funding;
    }
}
