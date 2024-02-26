package moa.cs.application;

import static moa.global.config.async.AsyncConfig.VIRTUAL_THREAD_EXECUTOR;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import lombok.RequiredArgsConstructor;
import moa.client.webhook.WebHookClient;
import moa.cs.domain.PersonalInquiry;
import moa.cs.domain.PersonalInquiryCreateEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Async(VIRTUAL_THREAD_EXECUTOR)
@Transactional(propagation = REQUIRES_NEW)
public class PersonalInquiryEventHandler {

    private static final String MESSAGE_FORMAT = """
            1대1 문의가 등록되었습니다
            문의 카테고리: %s
            문의 내용: %s
            문의자 ID: %s
            문의자 연락처: %s
            """;
    private final WebHookClient webHookClient;

    @TransactionalEventListener(value = PersonalInquiryCreateEvent.class, phase = AFTER_COMMIT)
    public void push(PersonalInquiryCreateEvent event) {
        PersonalInquiry inquiry = event.inquiry();
        webHookClient.sendContent(
                MESSAGE_FORMAT
                        .formatted(
                                inquiry.getCategory(),
                                inquiry.getContent(),
                                inquiry.getMember().getId(),
                                inquiry.getMember().getPhoneNumber()
                        )
        );
    }
}
