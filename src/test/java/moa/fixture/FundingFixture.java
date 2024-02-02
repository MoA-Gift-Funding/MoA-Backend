package moa.fixture;

import java.time.LocalDate;
import moa.funding.domain.Funding;
import moa.funding.domain.Visibility;
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
                "",
                "",
                LocalDate.now().plusDays(100),
                Visibility.PUBLIC,
                Price.from(maximumAmount),
                owner,
                product,
                null,
                ""
        );
    }

}
