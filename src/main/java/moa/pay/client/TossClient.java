package moa.pay.client;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import moa.pay.client.dto.TossPaymentCancelRequest;
import moa.pay.client.dto.TossPaymentConfirmRequest;
import moa.pay.client.dto.TossPaymentResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("https://api.tosspayments.com/v1/payments")
public interface TossClient {

    @PostExchange(url = "/confirm", contentType = APPLICATION_JSON_VALUE)
    TossPaymentResponse confirmPayment(
            @RequestHeader(AUTHORIZATION) String authorization,
            @RequestBody TossPaymentConfirmRequest request
    );

    @PostExchange(url = "/{paymentKey}/cancel", contentType = APPLICATION_JSON_VALUE)
    TossPaymentResponse cancelPayment(
            @PathVariable("paymentKey") String paymentKey,
            @RequestHeader(AUTHORIZATION) String authorization,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody TossPaymentCancelRequest request
    );
}
