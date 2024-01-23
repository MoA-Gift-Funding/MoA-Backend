package moa.funding.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import moa.funding.domain.Address;
import moa.funding.domain.FundingStatus;
import moa.funding.domain.Visibility;

public record FundingCreateCommand(
        Long memberId,
        Long productId,
        String title,
        String description,
        LocalDate endDate,
        BigDecimal maximumPrice,
        Address deliveryAddress,
        Visibility visible,
        FundingStatus status
) {

}
