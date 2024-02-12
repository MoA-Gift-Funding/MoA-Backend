package moa.global.sms;

import java.util.List;

public interface SmsSendEvent {

    String smsMessage();

    List<String> phoneNumbers();
}
