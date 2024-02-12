package moa.order.application;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import lombok.RequiredArgsConstructor;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingFinishEvent;
import moa.funding.domain.FundingRepository;
import moa.global.sms.SmsSender;
import moa.order.domain.Order;
import moa.order.domain.OrderRepository;
import moa.product.domain.Product;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

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
    private final SmsSender smsSender;
    private final ApplicationEventPublisher publisher;

    @Async("virtualThreadExecutor")
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(value = FundingFinishEvent.class, phase = AFTER_COMMIT)
    public void createOrder(FundingFinishEvent event) {
        Funding funding = fundingRepository.getById(event.fundingId());
        Order order = orderRepository.save(new Order(funding));

        String nickname = funding.getMember().getNickname();
        String title = funding.getTitle();
        Product product = funding.getProduct();
        String productName = product.getName();
        String deliveryUrl = product.getDeliveryUrl();
        String message = MESSAGE_FORMAT.formatted(nickname, title, productName, deliveryUrl);

        try {
            smsSender.send(message, funding.getAddress().getPhoneNumber());
            order.receive();
        } catch (Exception e) {
        }
    }
}

