package moa.order.application;

import lombok.RequiredArgsConstructor;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingFinishEvent;
import moa.funding.domain.FundingRepository;
import moa.member.domain.Member;
import moa.notification.application.NotificationService;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationFactory;
import moa.order.domain.Order;
import moa.order.domain.OrderRepository;
import moa.product.domain.Product;
import moa.sms.SmsMessageFactory;
import moa.sms.client.SmsClient;
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
    private final SmsMessageFactory smsMessageFactory;
    private final SmsClient smsClient;

    @Transactional
    @EventListener(value = FundingFinishEvent.class)
    public void placeOrder(FundingFinishEvent event) {
        Funding funding = fundingRepository.getById(event.fundingId());
        Order order = new Order(funding);
        orderRepository.save(order);
        Member member = order.getMember();
        Product product = funding.getProduct();
        Notification notification = notificationFactory.generateFundingFinishNotification(
                funding.getTitle(),
                product.getImageUrl(),
                order.getId(),
                member
        );
        notificationService.push(notification);

        String message = smsMessageFactory.generateFundingFinishMessage(
                funding.getTitle(),
                product.getName(),
                "링크 생성해서 처리" // TODO 상품 수령 링크 생성해서 처리
        );
        smsClient.send(message, member.getPhoneNumber());
    }
}
