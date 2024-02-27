package moa.global.domain;

import static java.math.RoundingMode.HALF_UP;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public record Price(
        @Column(name = "price", nullable = false) BigDecimal value
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

    public double divide(Price other) {
        return this.value.divide(other.value, 2, HALF_UP).doubleValue();
    }

    public long longValue() {
        return this.value.longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Price price)) {
            return false;
        }
        return value.compareTo(price.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
