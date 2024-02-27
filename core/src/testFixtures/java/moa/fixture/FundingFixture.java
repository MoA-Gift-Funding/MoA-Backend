package moa.fixture;

import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.time.LocalDate;
import moa.address.domain.Address;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingStatus;
import moa.funding.domain.FundingVisibility;
import moa.global.domain.Price;
import moa.member.domain.Member;
import moa.product.domain.Product;

public class FundingFixture {

    private static final Address address = new Address("", "", "", "", "", "", "");

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
        return new Funding(
                null,
                "",
                "",
                endDate,
                FundingVisibility.PUBLIC,
                Price.from(maximumAmount),
                owner,
                product,
                address,
                ""
        );
    }

    public static Funding funding(
            Member owner,
            Product product,
            FundingStatus status
    ) {
        Funding funding = new Funding(
                null,
                "",
                "",
                LocalDate.now().plusDays(10),
                FundingVisibility.PUBLIC,
                Price.from("5000"),
                owner,
                product,
                address,
                ""
        );
        setField(funding, "status", status);
        return funding;
    }
}
