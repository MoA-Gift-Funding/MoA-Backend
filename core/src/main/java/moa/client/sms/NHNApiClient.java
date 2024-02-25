package moa.client.sms;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "https://api-sms.cloud.toast.com")
public interface NHNApiClient {

    /**
     * https://docs.nhncloud.com/ko/Notification/SMS/ko/api-guide/#sms_1
     */
    @PostExchange(url = "/sms/v3.0/appKeys/{appKey}/sender/sms", contentType = APPLICATION_JSON_VALUE)
    NHNSendSmsResponse sendSms(
            @PathVariable(name = "appKey") String appKey,
            @RequestHeader(name = "X-Secret-Key") String secretKey,
            @RequestBody NHNSendSmsRequest sendSmsRequest
    );
}
