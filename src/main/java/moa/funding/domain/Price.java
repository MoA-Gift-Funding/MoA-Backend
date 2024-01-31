package moa.funding.domain;

import static java.math.RoundingMode.HALF_EVEN;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public record Price(
        @Column(name = "price") BigDecimal value
) {
    public static Price ZERO = new Price(BigDecimal.ZERO);

    public static Price from(String value) {
        return new Price(new BigDecimal(value));
    }

    public static Price from(Long value) {
        return new Price(BigDecimal.valueOf(value));
    }

    public boolean isGreaterThan(Price other) {
        return this.value.compareTo(other.value) > 0;
    }

    public boolean isLessThan(Price other) {
        return this.value.compareTo(other.value) < 0;
    }

    public Price add(Price price) {
        return new Price(this.value.add(price.value));
    }

    public Price minus(Price other) {
        return new Price(this.value.subtract(other.value));
    }

    public Price divide(Price other) {
        return new Price(this.value.divide(other.value, 2, HALF_EVEN));
    }

    public long longValue() {
        return this.value.longValue();
    }
}
