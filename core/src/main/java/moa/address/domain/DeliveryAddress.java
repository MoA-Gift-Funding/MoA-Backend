package moa.address.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.address.exception.DeliveryAddressExceptionType.NO_AUTHORITY_FOR_ADDRESS;
import static moa.address.exception.DeliveryAddressExceptionType.REQUIRED_DEFAULT_ADDRESS;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.address.exception.DeliveryAddressException;
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

    @Embedded
    private Address address;

    @Column
    private boolean isDefault;

    public DeliveryAddress(
            Member member,
            Address address,
            boolean isDefault
    ) {
        this.member = member;
        this.address = address;
        this.isDefault = isDefault;
    }

    public void validateOwner(Member member) {
        if (!this.member.equals(member)) {
            throw new DeliveryAddressException(NO_AUTHORITY_FOR_ADDRESS);
        }
    }

    public void update(
            Address address,
            boolean isDefault
    ) {
        if (this.isDefault && !isDefault) {
            throw new DeliveryAddressException(REQUIRED_DEFAULT_ADDRESS);
        }
        this.address = address;
        this.isDefault = isDefault;
    }

    public void unDefault() {
        this.isDefault = false;
    }

    public void delete() {
        if (isDefault) {
            throw new DeliveryAddressException(REQUIRED_DEFAULT_ADDRESS);
        }
    }

    public String getName() {
        return address.name();
    }

    public String getRecipientName() {
        return address.recipientName();
    }

    public String getPhoneNumber() {
        return address.phoneNumber();
    }
}
