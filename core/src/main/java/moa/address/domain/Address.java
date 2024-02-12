package moa.address.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Address(
        @Column(name = "zonecode") String zonecode,
        @Column(name = "road_address") String roadAddress,
        @Column(name = "jibun_address") String jibunAddress,
        @Column(name = "detail_address") String detailAddress
) {
}
