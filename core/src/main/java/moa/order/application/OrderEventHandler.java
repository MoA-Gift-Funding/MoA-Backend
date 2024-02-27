package moa.order.application;

import static moa.global.config.async.AsyncConfig.VIRTUAL_THREAD_EXECUTOR;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import moa.client.wincube.WincubeClient;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingFinishEvent;
import moa.funding.domain.FundingRepository;
import moa.notification.application.NotificationService;
import moa.notification.domain.Notification;
import moa.notification.domain.NotificationFactory;
import moa.order.domain.Order;
import moa.order.domain.OrderReadyEvent;
import moa.order.domain.OrderRepository;
import moa.order.domain.OrderTransaction;
import moa.order.domain.OrderTransactionRepository;
import moa.sms.SmsMessageFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class OrderEventHandler {

    private final OrderRepository orderRepository;
    private final FundingRepository fundingRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final WincubeClient wincubeClient;
    private final SmsMessageFactory smsMessageFactory;
    private final NotificationFactory notificationFactory;
    private final NotificationService notificationService;

    @Transactional
    @EventListener(value = FundingFinishEvent.class)
    public void createOrder(FundingFinishEvent event) {
        Funding funding = fundingRepository.getById(event.fundingId());
        Order order = new Order(funding);
        OrderTransaction orderTransaction = new OrderTransaction(order);
        orderRepository.save(order);
        orderTransactionRepository.save(orderTransaction);
    }

    @Async(VIRTUAL_THREAD_EXECUTOR)
    @TransactionalEventListener(value = OrderReadyEvent.class, phase = AFTER_COMMIT)
    public void issueCoupon(OrderReadyEvent event) {
        Order order = orderRepository.getById(event.order().getId());
        OrderTransaction orderTx = orderTransactionRepository.getLastedByOrder(order);
        String message = smsMessageFactory.generateFundingFinishMessage(
                order.getMember().getNickname(),
                order.getProduct().getProductName(),
                LocalDate.now().plusDays(order.getProduct().getLimitDate()).toString(),
                order.getProduct().getDescription()
        );
        Funding funding = order.getFunding();
        Notification notification = notificationFactory.generateFundingFinishNotification(
                funding.getTitle(),
                funding.getProduct().getImageUrl(),
                order.getId(),
                order.getMember()
        );
        notificationService.push(notification);
        wincubeClient.issueCoupon(
                orderTx.getTransactionId(),
                "[MoA] 모아 펀딩 달성 상품",
                message,
                order.getProduct().getProductId().getProductId(),
                order.getAddress().phoneNumber(),
                null
        );
        order.complete();
        orderRepository.save(order);
    }
}
