package moa.funding.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public record Price(
        @Column(name = "price") BigDecimal value
) {

    public static Price from(String value) {
        return new Price(new BigDecimal(value));
    }

    public static Price from(Long value) {
        return new Price(BigDecimal.valueOf(value));
    }

    public boolean isGreaterThan(Price price) {
        return this.value.compareTo(price.value) > 0;
    }

    public boolean isLessThan(Price price) {
        return this.value.compareTo(price.value) < 0;
    }

    public Long getValue() {
        return value.longValue();
    }

    public boolean isZero() {
        return this.value.compareTo(BigDecimal.ZERO) == 0;
    }
}
