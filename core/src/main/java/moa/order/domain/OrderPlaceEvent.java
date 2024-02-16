package moa.order.domain;

import static moa.notification.domain.NotificationType.PARTY;

import moa.funding.domain.Funding;
import moa.global.sms.SmsSendEvent;
import moa.notification.domain.NotificationEvent;
import moa.notification.domain.NotificationType;
import moa.product.domain.Product;

public class OrderPlaceEvent implements NotificationEvent, SmsSendEvent {

    private static final String NOTIFICATION_URL_FORMAT = "giftMoA://navigation?name=MyOrder&orderId=%l";
    private static final String NOTIFICATION_MESSAGE_FORMAT = "[] 펀딩이 달성 완료됐어요. 곧 펀딩 상품의 배송이 시작됩니다. 🎁";

    public static final String SMS_MESSAGE_FORMAT = """
            %s님이 등록하신 펀딩 [%s]이 달성 완료됐어요!
            다음 링크를 통해 정보를 입력하고 [%s]을(를) 수령해주세요 🥰
                
            수령하러 가기 🎁
            %s
            """;

    private Long memberId;
    private Order order;
    private String notificationTitle;
    private String notificationMessage;
    private String notificationImageUrl;

    private String smsMessage;
    private String phoneNumber;

    public OrderPlaceEvent(Order order) {
        this.order = order;
        setNotification(order);
        setSms(order);
    }

    private void setNotification(Order order) {
        this.memberId = order.getMember().getId();
        this.notificationTitle = "펀딩 달성";
        this.notificationMessage = NOTIFICATION_MESSAGE_FORMAT.formatted(order.getFunding().getTitle());
        this.notificationImageUrl = order.getProduct().getImageUrl();
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
    public Long memberId() {
        return memberId;
    }

    @Override
    public String notificationUrl() {
        // 생성자에서 바로 쓰면 Order 가 DB에 저장 전이라 ID가 없어서 오류가 발생함
        return NOTIFICATION_URL_FORMAT.formatted(order.getId());
    }

    @Override
    public String notificationTitle() {
        return notificationTitle;
    }

    @Override
    public String notificationMessage() {
        return notificationMessage;
    }

    @Override
    public String notificationImageUrl() {
        return notificationImageUrl;
    }

    @Override
    public NotificationType notificationType() {
        return PARTY;
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
