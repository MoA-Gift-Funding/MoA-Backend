package moa.global.sms;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class SmsSendEventListener {

    private final SmsSender smsSender;
    private final SmsHistoryRepository smsHistoryRepository;

    @Async("virtualThreadExecutor")
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(value = SmsSendEvent.class, phase = AFTER_COMMIT)
    public void createOrder(SmsSendEvent event) {
        SmsHistory smsHistory = new SmsHistory(event.smsMessage(), event.phoneNumber());
        smsHistoryRepository.save(smsHistory);
        try {
            smsSender.send(smsHistory.getMessage(), smsHistory.getPhoneNumber());
            smsHistory.send();
        } catch (Exception e) {
            smsHistory.error(e.getMessage());
        }
    }
}
