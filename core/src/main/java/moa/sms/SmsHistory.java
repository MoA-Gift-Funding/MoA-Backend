package moa.sms;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static moa.sms.SmsHistory.SmsStatus.BEFORE_SEND;
import static moa.sms.SmsHistory.SmsStatus.ERROR_OCCUR;
import static moa.sms.SmsHistory.SmsStatus.SEND;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String phoneNumber;

    private String errorMessage;

    @Enumerated(STRING)
    @Column(nullable = false)
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
