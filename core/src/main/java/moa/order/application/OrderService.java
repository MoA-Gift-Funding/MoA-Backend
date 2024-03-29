package moa.order.application;

import lombok.RequiredArgsConstructor;
import moa.client.wincube.WincubeClient;
import moa.member.domain.Member;
import moa.member.domain.MemberRepository;
import moa.order.application.command.CouponReissueCommand;
import moa.order.domain.Order;
import moa.order.domain.OrderRepository;
import moa.order.domain.OrderTransaction;
import moa.order.domain.OrderTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final WincubeClient wincubeClient;

    // TODO 이거 윈큐브 상품(or 쿠폰형 상품에 특화된 로직이라 나중에 상품 종류 추가되면 구조 변경)
    public void reissueCoupon(CouponReissueCommand command) {
        Order order = orderRepository.getWithLockById(command.orderId());
        Member member = memberRepository.getById(command.memberId());
        order.validateOwner(member);
        // TODO 윈큐브 기존 쿠폰 비활성화
        // TODO 윈큐브 쿠폰 발행 API 호출해서 쿠폰 발행하기(바뀐 번호로)
        order.reIssueCoupon(command.phoneNumber());
    }

    public void cancelCoupon(Long orderId, Long memberId) {
        Order order = orderRepository.getById(orderId);
        Member member = memberRepository.getById(memberId);
        order.validateOwner(member);
        OrderTransaction orderTx = orderTransactionRepository.getLastedByOrder(order);
        wincubeClient.cancelCoupon(orderTx.getTransactionId());
    }
}
