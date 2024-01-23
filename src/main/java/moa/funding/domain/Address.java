package moa.funding.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Address(
    @Column(name = "zonecode") String zoneCode,
    @Column(name = "road_address") String roadAddress,
    @Column(name = "jibun_address") String jibunAddress,
    @Column(name = "detail_address") String detailAddress
) {
}
