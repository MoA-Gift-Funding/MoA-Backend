package moa.address.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Address(
        @Column(name = "zonecode") String zonecode,
        @Column(name = "road_address") String roadAddress,
        @Column(name = "jibun_address") String jibunAddress,
        @Column(name = "detail_address") String detailAddress,
        @Column(name = "name") String name,
        @Column(name = "recipient_name") String recipientName,
        @Column(name = "phone_number") String phoneNumber
) {
    public Address changePhone(String phoneNumber) {
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
