package moa.funding.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import moa.delivery.domain.Delivery;
import moa.delivery.domain.DeliveryRepository;
import moa.funding.application.command.FundingCreateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class FundingService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final FundingRepository fundingRepository;
    private final DeliveryRepository deliveryRepository;

    public Long create(FundingCreateCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Product product = productRepository.getById(command.productId());
        Delivery delivery = deliveryRepository.getById(command.deliveryId());
        Funding funding = command.toFunding(member, product, delivery);
        funding.create();
        fundingRepository.save(funding);
        return funding.getId();
    }
}
