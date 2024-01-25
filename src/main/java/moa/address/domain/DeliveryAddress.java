package moa.address.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.global.domain.RootEntity;
import moa.member.domain.Member;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class DeliveryAddress extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String name;

    @Column
    private String recipientName;

    @Column
    private String phoneNumber;

    @Embedded
    private Address address;

    @Column
    private boolean isDefault;

    public DeliveryAddress(
            Member member,
            String name,
            String recipientName,
            String phoneNumber,
            String zonecode,
            String roadAddress,
            String jibunAddress,
            String detailAddress,
            boolean isDefault
    ) {
        this.member = member;
        this.name = name;
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.address = new Address(zonecode, roadAddress, jibunAddress, detailAddress);
        this.isDefault = isDefault;
    }

    public void makeDefault() {
        this.isDefault = true;
    }

    public void unDefault() {
        this.isDefault = false;
    }
}
