package moa.order.application;

import lombok.RequiredArgsConstructor;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingFinishEvent;
import moa.funding.domain.FundingRepository;
import moa.notification.application.NotificationService;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationFactory;
import moa.order.domain.Order;
import moa.order.domain.OrderRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderEventHandler {

    private final OrderRepository orderRepository;
    private final FundingRepository fundingRepository;
    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;

    @Transactional
    @EventListener(value = FundingFinishEvent.class)
    public void placeOrder(FundingFinishEvent event) {
        Funding funding = fundingRepository.getById(event.fundingId());
        Order order = new Order(funding);
        orderRepository.save(order);
        Notification notification = notificationFactory.generateFundingFinishNotification(
                funding.getTitle(),
                funding.getProduct().getImageUrl(),
                order.getId(),
                order.getMember()
        );
        notificationService.push(notification);
    }
}
