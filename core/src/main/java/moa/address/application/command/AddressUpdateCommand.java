package moa.address.application.command;

import moa.address.domain.Address;

public record AddressUpdateCommand(
        Long memberId,
        Long deliveryAddressId,
        String name,
        String recipientName,
        String phoneNumber,
        String zonecode,
        String roadAddress,
        String jibunAddress,
        String detailAddress,
        boolean isDefault
) {
    public Address toAddress() {
        return new Address(
                zonecode,
                roadAddress,
                jibunAddress,
                detailAddress,
                name,
                recipientName,
                phoneNumber
        );
    }
}
