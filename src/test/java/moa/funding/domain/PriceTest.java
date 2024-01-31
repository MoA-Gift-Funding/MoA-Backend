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
    void 나누기_시_반올림된다() {
        // given
        Price from = Price.from("10000");

        // when
        Price divide = from.divide(Price.from("6000"));

        // then
        assertThat(divide.longValue()).isEqualTo(2);
    }
}
