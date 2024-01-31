package moa.funding.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName(" (Price) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PriceTest {

    @Test
    void 나누기_시_소수점_둘째자리로_반올림된다() {
        // given
        Price from = Price.from("10000");

        // when
        double divide = from.divide(Price.from("6000"));

        // then
        assertThat(divide).isEqualTo(1.67);
    }

    @Test
    void 가격이_같으면_동일하다() {
        // given
        Price from1 = Price.from("10000.00");
        Price from2 = Price.from("10000");

        // when && then
        assertThat(from1).isEqualTo(from2);
    }
}
