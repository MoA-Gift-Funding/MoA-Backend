package moa.order.application;

import lombok.RequiredArgsConstructor;
import moa.client.wincube.WincubeClient;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingFinishEvent;
import moa.funding.domain.FundingRepository;
import moa.order.domain.Order;
import moa.order.domain.OrderRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderEventHandler {

    private final OrderRepository orderRepository;
    private final FundingRepository fundingRepository;
    private final WincubeClient wincubeClient;

    @EventListener(value = FundingFinishEvent.class)
    public void createOrder(FundingFinishEvent event) {
        Funding funding = fundingRepository.getWithLockById(event.fundingId());
        Order order = new Order(funding);
        orderRepository.save(order);
        // TODO 윈큐브 쿠폰 발행 API 호출해서 쿠폰 발행하기
        // TODO 실패된 경우 수령대기 상태로 변경
        // TODO 푸쉬알림 전송
    }
}
