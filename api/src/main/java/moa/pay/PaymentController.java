package moa.pay;

import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.pay.exception.TossPaymentExceptionType.PAYMENT_INVALID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.auth.Auth;
import moa.pay.client.PaymentProperty;
import moa.pay.client.TossClient;
import moa.pay.client.dto.TossPaymentConfirmRequest;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentConfirm;
import moa.pay.domain.TossPaymentConfirmRepository;
import moa.pay.domain.TossPaymentRepository;
import moa.pay.exception.TossPaymentException;
import moa.pay.request.PrepayRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController("/payments/toss")
public class PaymentController {

    private final TossClient tossClient;
    private final PaymentProperty paymentProperty;
    private final TossPaymentRepository tossPaymentRepository;
    private final TossPaymentConfirmRepository tossPaymentConfirmRepository;

    @PostMapping("/prepay")
    public ResponseEntity<Object> prepay(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody PrepayRequest request
    ) {
        var confirm = new TossPaymentConfirm(request.orderId(), request.amount(), memberId);
        tossPaymentConfirmRepository.save(confirm);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/success")
    public ResponseEntity<Void> paymentResult(
            @Valid @ModelAttribute TossPaymentConfirmRequest request
    ) {
        var tossPaymentConfirm = tossPaymentConfirmRepository.getById(request.orderId());
        tossPaymentConfirm.check(request.orderId(), request.amount());
        var response = tossClient.confirmPayment(
                paymentProperty.basicAuth(),
                request
        );
        TossPayment payment = response.toPayment(tossPaymentConfirm.getMemberId());
        tossPaymentRepository.save(payment);
        tossPaymentConfirmRepository.delete(tossPaymentConfirm);
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
