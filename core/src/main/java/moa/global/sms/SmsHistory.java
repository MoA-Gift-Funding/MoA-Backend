package moa.global.sms;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static moa.global.sms.SmsHistory.SmsStatus.BEFORE_SEND;
import static moa.global.sms.SmsHistory.SmsStatus.ERROR_OCCUR;
import static moa.global.sms.SmsHistory.SmsStatus.SEND;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.util.List;
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

    @Column(name = "phone_number")
    @ElementCollection(fetch = LAZY)
    @CollectionTable(
            name = "sms_phone_number",
            joinColumns = @JoinColumn(name = "sms_history_id")
    )
    private List<String> phoneNumbers;

    private SmsStatus status = BEFORE_SEND;

    private String errorMessage;

    public SmsHistory(String message, List<String> phoneNumbers) {
        this.message = message;
        this.phoneNumbers = phoneNumbers;
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
