package moa.funding.domain;

import static moa.funding.exception.FundingExceptionType.INVALID_MINIMUM_PRICE;

import java.math.BigDecimal;
import moa.funding.exception.FundingException;
import org.springframework.stereotype.Component;

@Component
public class FundingValidator {

    /**
     * minimumPrice가 price보다 크면 1 minimumPrice가 price보다 작으면 -1 minimumPrice가 price와 같으면 0
     */
    public void validateFundingPrice(Funding funding, BigDecimal minimumPrice) {
        // 최소 펀딩 금액이 기준과 다르면 예외
        if (minimumPrice.compareTo(funding.getMinimumPrice()) != 0) {
            throw new FundingException(INVALID_MINIMUM_PRICE);
        }

        // 최대 펀딩 금액이 0이면 무제한이므로 검증하지 않음
        if (funding.getMaximumPrice().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        // 최대 펀딩 금액이 최소 펀딩 금액보다 작으면 예외
        if (minimumPrice.compareTo(funding.getMaximumPrice()) > 0) {
            throw new FundingException(INVALID_MINIMUM_PRICE);
        }
    }
}
