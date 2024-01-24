package moa.funding.domain;

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

    public boolean isGreaterThan(Price price) {
        return this.value.compareTo(price.value) > 0;
    }

    public boolean isLessThan(Price price) {
        return this.value.compareTo(price.value) < 0;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Price add(Price price) {
        return new Price(this.value.add(price.value));
    }

    public Price divide(Price price) {
        return new Price(this.value.divide(price.value));
    }
}
