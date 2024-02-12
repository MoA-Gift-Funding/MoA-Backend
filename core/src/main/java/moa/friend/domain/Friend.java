package moa.friend.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.friend.exception.FriendExceptionType.NO_AUTHORITY_FOR_FRIEND;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.friend.exception.FriendException;
import moa.global.domain.RootEntity;
import moa.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "unique_member_id_and_target_id", columnNames = {"member_id", "target_id"})}
)
public class Friend extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "target_id")
    private Member target;

    @Column(nullable = false)
    private String nickname;

    @Column
    private boolean isBlocked = false;

    public Friend(Member member, Member target, String nickname) {
        this.member = member;
        this.target = target;
        this.nickname = nickname;
    }

    public void validateAuthority(Member member) {
        if (!this.member.equals(member)) {
            throw new FriendException(NO_AUTHORITY_FOR_FRIEND);
        }
    }

    public void update(String nickname) {
        this.nickname = nickname;
    }

    public void block() {
        this.isBlocked = true;
    }

    public void unblock() {
        this.isBlocked = false;
    }
}
