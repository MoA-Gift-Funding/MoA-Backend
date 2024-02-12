package moa.order.application;

import lombok.RequiredArgsConstructor;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingFinishEvent;
import moa.funding.domain.FundingRepository;
import moa.order.domain.Order;
import moa.order.domain.OrderRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderEventHandler {

    public static final String MESSAGE_FORMAT = """
            %s님이 등록하신 펀딩 [%s]이 달성 완료됐어요!
            다음 링크를 통해 정보를 입력하고 [%s]을(를) 수령해주세요 🥰
                
            수령하러 가기 🎁
            %s
            """;

    private final OrderRepository orderRepository;
    private final FundingRepository fundingRepository;

    @Transactional
    @EventListener(value = FundingFinishEvent.class)
    public void placeOrder(FundingFinishEvent event) {
        Funding funding = fundingRepository.getById(event.fundingId());
        Order order = new Order(funding);
        orderRepository.save(order);
    }
}

