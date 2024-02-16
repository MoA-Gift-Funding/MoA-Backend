package moa.order.domain;

import moa.funding.domain.Funding;
import moa.product.domain.Product;
import moa.sms.SmsSendEvent;

// TODO 이것도 직접 문자 전송하도록 변경
public class OrderPlaceEvent implements SmsSendEvent {

    public static final String SMS_MESSAGE_FORMAT = """
            %s님이 등록하신 펀딩 [%s]이 달성 완료됐어요!
            다음 링크를 통해 정보를 입력하고 [%s]을(를) 수령해주세요 🥰
                
            수령하러 가기 🎁
            %s
            """;

    private String smsMessage;
    private String phoneNumber;

    public OrderPlaceEvent(Order order) {
        setSms(order);
    }

    private void setSms(Order order) {
        Funding funding = order.getFunding();
        String nickname = funding.getMember().getNickname();
        String title = funding.getTitle();
        Product product = funding.getProduct();
        String productName = product.getName();
        String deliveryUrl = product.getDeliveryUrl();
        this.smsMessage = SMS_MESSAGE_FORMAT.formatted(nickname, title, productName, deliveryUrl);
        this.phoneNumber = order.getMember().getPhoneNumber();
    }

    @Override
    public String smsMessage() {
        return smsMessage;
    }

    @Override
    public String phoneNumber() {
        return phoneNumber;
    }
}
