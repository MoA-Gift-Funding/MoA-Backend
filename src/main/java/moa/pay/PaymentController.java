package moa.pay;

import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.pay.TossPaymentExceptionType.PAYMENT_INVALID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.auth.Auth;
import moa.pay.TossClient.TossPaymentConfirmRequest;
import moa.pay.util.Base64Util;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Transactional
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final TossClient tossClient;
    private final PaymentProperty paymentProperty;
    private final OrderDetailRedisRepository orderDetailRedisRepository;

    @PostMapping("/prepay")
    public ResponseEntity<Object> prepay(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @RequestBody PrepayRequest request
    ) {
        OrderDetail orderDetail = new OrderDetail(request.orderId(), request.amount(), memberId, 10);
        orderDetailRedisRepository.save(orderDetail);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/success")
    public ResponseEntity<Void> paymentResult(
            @ModelAttribute TossPaymentRequest request
    ) {
        OrderDetail orderDetail = orderDetailRedisRepository.getById(request.orderId());

        if (orderDetail.isValid(request.orderId(), request.amount())) {
            TossPayment response = tossClient.confirmPayment(
                    request.paymentKey(),
                    "Basic " + Base64Util.parseToBase64(paymentProperty.secretKey()),
                    new TossPaymentConfirmRequest(request.orderId(), request.amount())
            );
            // TODO: response save
        } else {
            log.info("토스페이먼츠 결제 오류 {}", request);
            throw new TossPaymentException(PAYMENT_INVALID.withDetail("orderId:" + request.orderId()));
        }
        orderDetailRedisRepository.delete(orderDetail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fail")
    public ResponseEntity<Void> paymentResult(
            @RequestParam(value = "message") String message,
            @RequestParam(value = "code") Integer code
    ) {
        throw new TossPaymentException(PAYMENT_INVALID.withDetail("message:" + message + ", code:" + code));
    }
}
