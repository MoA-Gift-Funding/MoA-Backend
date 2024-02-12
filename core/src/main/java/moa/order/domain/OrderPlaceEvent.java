package moa.order.domain;

import moa.notification.domain.NotificationEvent;

public class OrderPlaceEvent implements NotificationEvent {

    private static final String URL_FORMAT = "giftMoA://navigation?name=MyOrder&orderId=%l";
    private static final String MESSAGE_FORMAT = "[] 펀딩이 달성 완료됐어요. 곧 펀딩 상품의 배송이 시작됩니다. 🎁";

    private Long memberId;
    private String url;
    private String title;
    private String message;
    private String imageUrl;

    public OrderPlaceEvent(Order order) {
        this.memberId = order.getMember().getId();
        this.url = URL_FORMAT.formatted(order.getId());
        this.title = "펀딩 달성";
        this.message = MESSAGE_FORMAT.formatted(order.getFunding().getTitle());
        this.imageUrl = order.getProduct().getImageUrl();
    }

    @Override
    public Long memberId() {
        return memberId;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public String imageUrl() {
        return imageUrl;
    }
}
