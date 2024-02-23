package moa.pay.application;

import lombok.RequiredArgsConstructor;
import moa.pay.application.command.PaymentPermitCommand;
import moa.pay.application.command.TempPaymentSaveCommand;
import moa.pay.client.TossClient;
import moa.pay.client.dto.TossPaymentResponse;
import moa.pay.domain.TemporaryTossPayment;
import moa.pay.domain.TemporaryTossPaymentRepository;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossClient tossClient;
    private final TossPaymentRepository paymentRepository;
    private final TemporaryTossPaymentRepository temporaryTossPaymentRepository;

    public void saveTemp(TempPaymentSaveCommand command) {
        TemporaryTossPayment tempPayment = command.toTemporaryPayment();
        temporaryTossPaymentRepository.save(tempPayment);
    }

    public void permitPayment(PaymentPermitCommand command) {
        TemporaryTossPayment prePayment = temporaryTossPaymentRepository.getById(command.orderId());
        prePayment.check(command.orderId(), command.amount(), command.memberId());
        TossPaymentResponse response = tossClient.confirmPayment(command.toConfirmRequest());
        TossPayment payment = response.toPayment(command.memberId());
        paymentRepository.save(payment);
        temporaryTossPaymentRepository.delete(prePayment);
    }

    public void cancelPayment(String paymentOrderId, String reason) {
        TossPayment payment = paymentRepository.getByOrderId(paymentOrderId);
        payment.pendingCancel(reason);
        paymentRepository.save(payment);
        tossClient.cancelPayment(payment);
        payment.cancel(reason);
        paymentRepository.save(payment);
    }
}
