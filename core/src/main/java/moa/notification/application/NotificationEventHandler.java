package moa.notification.application;

import static moa.global.config.async.AsyncConfig.VIRTUAL_THREAD_EXECUTOR;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import lombok.RequiredArgsConstructor;
import moa.notification.application.command.NotificationPushCommand;
import moa.notification.domain.NotificationEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationService notificationService;

    @Async(VIRTUAL_THREAD_EXECUTOR)
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(value = NotificationEvent.class, phase = AFTER_COMMIT)
    public void push(NotificationEvent event) {
        notificationService.push(new NotificationPushCommand(
                event.memberId(),
                event.notificationUrl(),
                event.notificationTitle(),
                event.notificationMessage(),
                event.notificationImageUrl()
        ));
    }
}
