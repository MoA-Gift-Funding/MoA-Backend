package moa.fixture;

import static moa.funding.domain.FundingVisibility.PUBLIC;

import java.time.LocalDate;
import moa.funding.domain.Funding;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.product.domain.Product;

public class FundingFixture {

    public static Funding funding(
            Member owner,
            Product product,
            String maximumAmount
    ) {
        return new Funding(
                null,
                "",
                "",
                LocalDate.now().plusWeeks(4),
                PUBLIC,
                Price.from(maximumAmount),
                owner,
                product,
                null,
                ""
        );
    }

}
