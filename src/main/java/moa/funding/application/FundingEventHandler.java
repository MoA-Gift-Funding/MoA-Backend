package moa.funding.application;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import lombok.RequiredArgsConstructor;
import moa.funding.domain.FundingCancelEvent;
import moa.funding.domain.FundingParticipant;
import moa.funding.domain.FundingParticipantRepository;
import moa.pay.client.PaymentProperty;
import moa.pay.client.TossClient;
import moa.pay.client.dto.TossPaymentCancelRequest;
import moa.pay.domain.TossPayment;
import moa.pay.domain.TossPaymentCancel;
import moa.pay.domain.TossPaymentCancelRepository;
import moa.pay.domain.TossPaymentRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class FundingEventHandler {

    private final TossClient tossClient;
    private final FundingParticipantRepository fundingParticipantRepository;
    private final TossPaymentCancelRepository tossPaymentCancelRepository;
    private final TossPaymentRepository tossPaymentRepository;
    private final PaymentProperty paymentProperty;

    @Async
    @TransactionalEventListener(value = FundingCancelEvent.class, phase = AFTER_COMMIT)
    public void cancelFunding(FundingCancelEvent event) {
        var participants = fundingParticipantRepository.findWithPaymentByFundingId(event.fundingId());
        for (FundingParticipant participant : participants) {
            TossPayment tossPayment = participant.getTossPayment();
            TossPaymentCancel tossPaymentCancel = new TossPaymentCancel(tossPayment);
            tossPaymentCancelRepository.save(tossPaymentCancel);
            tossClient.cancelPayment(
                    tossPayment.getPaymentKey(),
                    paymentProperty.basicAuth(),
                    tossPaymentCancel.getIdempotencyKey(),
                    new TossPaymentCancelRequest("펀딩 개설자의 펀딩 취소로 인한 결제 취소")
            );
            tossPayment.cancel();
            tossPaymentRepository.save(tossPayment);
        }
    }
}
