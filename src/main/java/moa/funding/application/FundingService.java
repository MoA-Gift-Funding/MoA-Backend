package moa.funding.application;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import moa.funding.application.command.FundingCreateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.funding.domain.FundingValidator;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class FundingService {

    @Value("${funding.price.min}")
    private String minimumPriceValue;

    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;
    private final FundingValidator fundingValidator;

    public Funding create(FundingCreateCommand command) {
        Member member = memberRepository.getById(command.memberId());
        BigDecimal minimumPrice = new BigDecimal(minimumPriceValue);
        Funding funding = new Funding(
            command.title(),
            command.description(),
            command.endDate(),
            command.maximumPrice(),
            minimumPrice,
            command.deliveryAddress(),
            command.visible(),
            command.status(),
            member
        );
        fundingValidator.validateFundingPrice(funding, minimumPrice);
        return fundingRepository.save(funding);
    }
}
