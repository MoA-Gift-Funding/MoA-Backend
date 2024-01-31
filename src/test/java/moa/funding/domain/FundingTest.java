package moa.funding.domain;

import static moa.funding.domain.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import moa.member.domain.Member;
import moa.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("펀딩 (Funding) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FundingTest {

    @Nested
    class 펀딩_진행률_계산_시 {

        @ParameterizedTest(name = "펀딩 상품이 {0}원일 때, 지금까지 모인 금액이 {1} 원이면, 진행률은 {2}% 다.")
        @CsvSource(value = {
                "300000, 10000, 3",
                "100000, 30000, 30"
        }, delimiterString = ", ")
        void 계산한다(String productPrice, String fundedAmount, String rate) {
            // given
            Funding funding = new Funding(
                    "",
                    "",
                    LocalDate.now(),
                    PUBLIC,
                    Price.from(productPrice),
                    mock(Member.class),
                    new Product("", Price.from(productPrice)),
                    null,
                    ""
            );
            funding.participate(null, Price.from(fundedAmount), "");

            // when
            long fundingRate = funding.getFundingRate();

            // then
            assertThat(fundingRate).isEqualTo(Long.valueOf(rate));
        }
    }
}
