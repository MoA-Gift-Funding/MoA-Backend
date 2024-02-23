package moa.order.application;

import lombok.RequiredArgsConstructor;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingRepository;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.order.application.command.CouponReissueCommand;
import moa.order.application.command.OrderPlaceCommand;
import moa.order.domain.Order;
import moa.order.domain.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final FundingRepository fundingRepository;
    private final MemberRepository memberRepository;

    public Long place(OrderPlaceCommand command) {
        Funding funding = fundingRepository.getWithLockById(command.memberId());
        Member member = memberRepository.getById(command.memberId());
        funding.validateOwner(member);
        Order order = new Order(funding);
        // TODO 윈큐브 쿠폰 발행 API 호출해서 쿠폰 발행하기
        return orderRepository.save(order)
                .getId();
    }

    // TODO 이거 윈큐브 상품(or 쿠폰형 상품에 특화된 로직이라 나중에 상품 종류 추가되면 구조 변경)
    public void reissueCoupon(CouponReissueCommand command) {
        Order order = orderRepository.getWithLockById(command.orderId());
        Member member = memberRepository.getById(command.memberId());
        order.validateOwner(member);
        // TODO 윈큐브 기존 쿠폰 비활성화
        // TODO 윈큐브 쿠폰 발행 API 호출해서 쿠폰 발행하기(바뀐 번호로)
        order.reIssueCoupon(command.phoneNumber());
    }
}
