package moa.order.query.response;

import java.time.LocalDateTime;
import java.util.List;
import moa.address.domain.Address;
import moa.friend.domain.Friend;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingParticipant;
import moa.member.domain.Member;
import moa.order.domain.Order;
import moa.product.domain.Product;
import moa.product.domain.ProductId;

public record OrderDetailResponse(
        Long orderId,
        LocalDateTime orderDate,
        ProductId productId,
        String imageUrl,
        String brand,
        String category,
        String productName,
        Long price,
        Address address,
        String deliveryRequestMessage,
        PaymentResponse payment
) {
    public static OrderDetailResponse of(Order order, List<Friend> friends) {
        Product product = order.getProduct();
        Funding funding = order.getFunding();
        return new OrderDetailResponse(
                order.getId(),
                order.getCreatedDate(),
                product.getProductId(),
                product.getImageUrl(),
                product.getBrand(),
                product.getCategory(),
                product.getProductName(),
                product.getPrice().longValue(),
                order.getAddress(),
                order.getDeliveryRequestMessage(),
                PaymentResponse.of(funding, friends)
        );
    }

    public record PaymentResponse(
            List<ParticipantPaymentResponse> participantPayments,
            MyPaymentResponse myPayment
    ) {

        public static PaymentResponse of(Funding funding, List<Friend> friends) {
            List<FundingParticipant> participants = funding.getParticipants();
            List<ParticipantPaymentResponse> participantPayments = participants.stream()
                    .map(it -> new ParticipantPaymentResponse(
                            it.getMember().getId(),
                            getNickName(it.getMember(), friends),
                            it.getMember().getNickname(),
                            it.getAmount().longValue()
                    )).toList();
            Long myPayment = funding.getProduct().getPrice()
                    .minus(funding.getFundedAmount())
                    .longValue();
            return new PaymentResponse(participantPayments, new MyPaymentResponse(myPayment));
        }

        private static String getNickName(Member member, List<Friend> friends) {
            return friends.stream()
                    .filter(friend -> friend.getTarget().equals(member))
                    .findAny()
                    .map(Friend::getNickname)
                    .orElseGet(member::getNickname);
        }

        public record ParticipantPaymentResponse(
                Long memberId,
                String customNickname,
                String realNickname,
                Long amount
        ) {
        }

        public record MyPaymentResponse(
                Long amount
        ) {
        }
    }
}
