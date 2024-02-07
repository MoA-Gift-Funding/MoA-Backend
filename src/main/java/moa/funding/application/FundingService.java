package moa.funding.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import moa.address.domain.DeliveryAddress;
import moa.address.domain.DeliveryAddressRepository;
import moa.funding.application.command.FundingCreateCommand;
import moa.funding.application.command.FundingFinishCommand;
import moa.funding.application.command.FundingParticipateCommand;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingParticipant;
import moa.funding.domain.FundingRepository;
import moa.funding.domain.FundingValidator;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentRepository;
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
    private final TossPaymentRepository tossPaymentRepository;
    private final FundingValidator fundingValidator;

    public Long create(FundingCreateCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Product product = productRepository.getById(command.productId());
        DeliveryAddress address = addressRepository.getById(command.deliveryAddressId());
        address.validateOwner(member);
        Funding funding = command.toFunding(member, product, address);
        funding.create();
        return fundingRepository.save(funding).getId();
    }

    // TODO 락 걸어서 동시 접근 제한, 예외 발생 시 결제 취소
    public void participate(FundingParticipateCommand command) {
        Funding funding = fundingRepository.getWithLockById(command.fundingId());
        Member member = memberRepository.getById(command.memberId());
        fundingValidator.validateVisible(member, funding);
        TossPayment payment = tossPaymentRepository.getByOrderId(command.paymentOrderId());
        payment.use(member.getId());
        FundingParticipant participant = new FundingParticipant(member, funding, payment, command.message());
        funding.participate(participant);
        fundingRepository.save(funding);
    }

    // TODO 락 걸어서 동시 접근 제한, 예외 발생 시 결제 취소
    public void finish(FundingFinishCommand command) {
        Funding funding = fundingRepository.getWithLockById(command.fundingId());
        Member member = memberRepository.getById(command.memberId());
        TossPayment payment = tossPaymentRepository.getByOrderId(command.paymentOrderId());
        payment.use(member.getId());
        funding.validateOwner(member);
        funding.finish(payment.getTotalAmount());
        fundingRepository.save(funding);
    }

    public void cancel(Long fundingId, Long memberId) {
        Funding funding = fundingRepository.getById(fundingId);
        Member member = memberRepository.getById(memberId);
        funding.validateOwner(member);
        funding.cancel();
        fundingRepository.save(funding);
    }
}
