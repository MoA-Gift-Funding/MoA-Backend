package moa.member.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.member.domain.MemberStatus.PRESIGNED_UP;
import static moa.member.domain.MemberStatus.SIGNED_UP;
import static moa.member.domain.MemberStatus.WITHDRAW;
import static moa.member.exception.MemberExceptionType.ALREADY_SIGNED_UP;
import static moa.member.exception.MemberExceptionType.ALREADY_WITHDRAW;
import static moa.member.exception.MemberExceptionType.NOT_VERIFIED_PHONE;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.global.domain.RootEntity;
import moa.member.domain.phone.Phone;
import moa.member.exception.MemberException;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE member SET status = 'WITHDRAW' WHERE id = ?")
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "oauth_id_unique",
                columnNames = {
                        "oauth_id",
                        "oauth_provider"
                }
        ),
})
public class Member extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private OauthId oauthId;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = true)
    private String birthyear;

    @Column(nullable = true)
    private String birthday;

    @Column(nullable = true)
    private String profileImageUrl;

    @Embedded
    private Phone phone;

    @Enumerated(STRING)
    private MemberStatus status;

    @Column(unique = true)
    private String tossCustomerKey;

    public Member(
            OauthId oauthId,
            String email,
            String nickname,
            String birthyear,
            String birthday,
            String profileImageUrl,
            String phoneNumber
    ) {
        this.oauthId = oauthId;
        this.email = email;
        this.nickname = nickname;
        this.birthyear = birthyear;
        this.birthday = birthday;
        this.profileImageUrl = profileImageUrl;
        this.phone = new Phone(this, phoneNumber);
        this.tossCustomerKey = UUID.randomUUID().toString();
    }

    public void preSignup(MemberValidator validator) {
        validator.validateDuplicatedEmail(email);
        this.status = PRESIGNED_UP;
    }

    public void changeVerifiedPhone(
            MemberValidator memberValidator,
            Phone phone
    ) {
        if (!phone.isVerified()) {
            throw new MemberException(NOT_VERIFIED_PHONE);
        }
        memberValidator.validateDuplicatedVerifiedPhone(phone);
        this.phone = phone;
    }

    public void signup(
            MemberValidator memberValidator,
            String email,
            String nickname,
            String birthday,
            String birthyear,
            String profileImageUrl
    ) {
        if (status == SIGNED_UP) {
            throw new MemberException(ALREADY_SIGNED_UP);
        }
        if (!phone.isVerified()) {
            throw new MemberException(NOT_VERIFIED_PHONE);
        }
        memberValidator.validateDuplicatedEmail(email);
        this.status = SIGNED_UP;
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
        this.birthyear = birthyear;
        this.profileImageUrl = profileImageUrl;
    }

    public void update(
            String nickname,
            String birthyear,
            String birthday,
            String profileImageUrl
    ) {
        this.nickname = nickname;
        this.birthyear = birthyear;
        this.birthday = birthday;
        this.profileImageUrl = profileImageUrl;
    }

    public void permitNotification(String deviceToken) {
        getPhone().permitNotification(deviceToken);
    }

    public void rejectNotification() {
        getPhone().rejectNotification();
    }

    public String getPhoneNumber() {
        return getPhone().getPhoneNumber();
    }

    public void withdraw() {
        if (status == WITHDRAW) {
            throw new MemberException(ALREADY_WITHDRAW);
        }
        this.oauthId = null;
        this.email = null;
        this.nickname = "탈퇴한 회원";
        this.birthyear = null;
        this.birthday = null;
        this.profileImageUrl = null;
        this.phone = null;
        this.tossCustomerKey = null;
        this.status = WITHDRAW;
        registerEvent(new MemberWithdrawnEvent(this.getId()));
    }
}
