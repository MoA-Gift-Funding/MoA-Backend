package moa.funding.domain;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import moa.funding.exception.FundingException;
import static moa.funding.exception.FundingExceptionType.INVALID_MINIMUM_PRICE;

@Component
public class FundingValidator {

    @Value("${funding.price.minimum}")
    private String minimumPrice;

    /**
     * MINIMUM_PRICE가 price보다 크면 1
     * MINIMUM_PRICE가 price보다 작으면 -1
     * MINIMUM_PRICE가 price와 같으면 0
     */
    public void validateFundingPrice(Funding funding) {
        // 최소 펀딩 금액이 기준보다 작으면 예외
        if (new BigDecimal(minimumPrice).compareTo(funding.getMinimumPrice()) < 0) {
            throw new FundingException(INVALID_MINIMUM_PRICE);
        }

        // 최대 펀딩 금액이 최소 펀딩 금액보다 작으면 예외
        if (new BigDecimal(minimumPrice).compareTo(funding.getMaximumPrice()) < 0) {
            throw new FundingException(INVALID_MINIMUM_PRICE);
        }
    }
}
