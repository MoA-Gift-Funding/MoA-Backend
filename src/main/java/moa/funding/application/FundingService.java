package moa.funding.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import moa.address.domain.DeliveryAddress;
import moa.address.domain.DeliveryAddressRepository;
import moa.funding.application.command.FundingCreateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.product.domain.Product;
import moa.product.domain.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FundingService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final FundingRepository fundingRepository;
    private final DeliveryAddressRepository addressRepository;

    public Long create(FundingCreateCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Product product = productRepository.getById(command.productId());
        DeliveryAddress address = addressRepository.getById(command.deliveryAddressId());
        address.validateOwner(member);
        Funding funding = command.toFunding(member, product, address);
        funding.create();
        fundingRepository.save(funding);
        return funding.getId();
    }
}
