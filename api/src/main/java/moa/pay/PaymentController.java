package moa.pay;

import static moa.member.domain.MemberStatus.SIGNED_UP;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moa.auth.Auth;
import moa.pay.application.TossPaymentService;
import moa.pay.request.PermitPaymentRequest;
import moa.pay.request.PrepayRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments/toss")
public class PaymentController implements PaymentApi {

    private final TossPaymentService tossPaymentService;

    @PostMapping("/prepay")
    public ResponseEntity<Void> prepay(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @RequestBody PrepayRequest request
    ) {
        tossPaymentService.saveTemp(request.toCommand(memberId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/success")
    public ResponseEntity<Void> permitPayment(
            @Auth(permit = {SIGNED_UP}) Long memberId,
            @Valid @ModelAttribute PermitPaymentRequest request
    ) {
        tossPaymentService.permitPayment(request.toCommand(memberId));
        return ResponseEntity.ok().build();
    }
}
