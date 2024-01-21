package moa.member.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static moa.member.exception.MemberExceptionType.NOT_VERIFIED_PHONE;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.global.domain.RootEntity;
import moa.member.domain.phone.Phone;
import moa.member.exception.MemberException;
import org.hibernate.annotations.SQLDelete;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET status = 'WITHDRAW' WHERE id = ?")
@Entity
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

    @Builder
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
    }

    public void preSignup(MemberValidator validator) {
        validator.validateDuplicatedEmailExceptMe(email, id);
        this.status = MemberStatus.PRESIGNED_UP;
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
        if (!phone.isVerified()) {
            throw new MemberException(NOT_VERIFIED_PHONE);
        }
        memberValidator.validateDuplicatedEmailExceptMe(email, id);
        this.status = MemberStatus.SIGNED_UP;
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

    public String getPhoneNumber() {
        return getPhone().getPhoneNumber();
    }
}
