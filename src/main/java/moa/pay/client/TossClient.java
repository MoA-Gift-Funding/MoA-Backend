package moa.pay.client;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import moa.pay.client.dto.TossPaymentConfirmRequest;
import moa.pay.client.dto.TossPaymentConfirmResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;

public interface TossClient {

    @PostExchange(url = "https://api.tosspayments.com/v1/payments/{paymentKey}", contentType = APPLICATION_JSON_VALUE)
    TossPaymentConfirmResponse confirmPayment(
            @PathVariable("paymentKey") String paymentKey,
            @RequestHeader(AUTHORIZATION) String authorization,
            @RequestBody TossPaymentConfirmRequest request
    );
}
