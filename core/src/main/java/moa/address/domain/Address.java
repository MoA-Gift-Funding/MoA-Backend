package moa.address.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Address(
        @Column(name = "zonecode", nullable = false) String zonecode,
        @Column(name = "road_address", nullable = false) String roadAddress,
        @Column(name = "jibun_address", nullable = false) String jibunAddress,
        @Column(name = "detail_address", nullable = false) String detailAddress,
        @Column(name = "name", nullable = false) String name,
        @Column(name = "recipient_name", nullable = false) String recipientName,
        @Column(name = "phone_number", nullable = false) String phoneNumber
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
