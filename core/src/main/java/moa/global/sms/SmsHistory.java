package moa.global.sms;

import static jakarta.persistence.GenerationType.IDENTITY;
import static moa.global.sms.SmsHistory.SmsStatus.BEFORE_SEND;
import static moa.global.sms.SmsHistory.SmsStatus.ERROR_OCCUR;
import static moa.global.sms.SmsHistory.SmsStatus.SEND;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.global.domain.RootEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SmsHistory extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String message;
    private String phoneNumber;
    private String errorMessage;
    private SmsStatus status = BEFORE_SEND;

    public SmsHistory(String message, String phoneNumber) {
        this.message = message;
        this.phoneNumber = phoneNumber;
    }

    public enum SmsStatus {
        BEFORE_SEND,
        SEND,
        ERROR_OCCUR
    }

    public void send() {
        this.status = SEND;
    }

    public void error(String message) {
        this.status = ERROR_OCCUR;
        this.errorMessage = message;
    }
}
