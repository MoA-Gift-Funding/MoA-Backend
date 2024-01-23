package moa.funding.application;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import moa.funding.application.command.FundingCreateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.funding.domain.FundingValidator;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class FundingService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final FundingRepository fundingRepository;
    private final FundingValidator fundingValidator;

    public Funding create(FundingCreateCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Product product = productRepository.getById(command.productId());
        Funding funding = command.toFunding(member, product);
        fundingValidator.validateFundingPrice(funding, Funding.MINIMUM_PRICE);
        return fundingRepository.save(funding);
    }
}
