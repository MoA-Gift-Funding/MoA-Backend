package moa.funding.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Price {

    @Column(name = "price")
    private BigDecimal value;

    public Price(String value) {
        this.value = new BigDecimal(value);
    }

    public Price(Long value) {
        this.value = BigDecimal.valueOf(value);
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
