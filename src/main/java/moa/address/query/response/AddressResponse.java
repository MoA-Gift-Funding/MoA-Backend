package moa.address.query.response;

import io.swagger.v3.oas.annotations.media.Schema;
import moa.address.domain.Address;
import moa.address.domain.DeliveryAddress;

public record AddressResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "여의도집") String name,
        @Schema(example = "이수진") String recipientName,
        @Schema(example = "010-1111-1111") String phoneNumber,
        @Schema(example = "01234") String zonecode,
        @Schema(example = "서울특별시 도봉구 도봉로 100길 12") String roadAddress,
        @Schema(example = "서울특별시 도봉구 방학동") String jibunAddress,
        @Schema(example = "xx아파트 xx동 xx호") String detailAddress,
        @Schema(description = "기본 배송지 여부") boolean isDefault
) {
    public static AddressResponse from(DeliveryAddress deliveryAddress) {
        Address address = deliveryAddress.getAddress();
        return new AddressResponse(
                deliveryAddress.getId(),
                deliveryAddress.getName(),
                deliveryAddress.getRecipientName(),
                deliveryAddress.getPhoneNumber(),
                address.zonecode(),
                address.roadAddress(),
                address.jibunAddress(),
                address.detailAddress(),
                deliveryAddress.isDefault()
        );
    }
}
