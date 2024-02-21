package moa.address.application.command;

import moa.address.domain.Address;
import moa.address.domain.DeliveryAddress;
import moa.member.domain.Member;

public record AddressCreateCommand(
        Long memberId,
        String name,
        String recipientName,
        String phoneNumber,
        String zonecode,
        String roadAddress,
        String jibunAddress,
        String detailAddress,
        boolean isDefault
) {
    public DeliveryAddress toAddress(Member member) {
        return new DeliveryAddress(
                member,
                new Address(
                        zonecode,
                        roadAddress,
                        jibunAddress,
                        detailAddress,
                        name,
                        recipientName,
                        phoneNumber
                ),
                isDefault
        );
    }
}
