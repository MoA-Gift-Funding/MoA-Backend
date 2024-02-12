package moa.notification.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

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
public class Notification extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String url;

    private String title;

    private String message;

    private String imageUrl;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean isRead = false;

    public Notification(
            String url,
            String title,
            String message,
            String imageUrl,
            Member member
    ) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
        this.member = member;
    }

    public void read() {
        this.isRead = true;
    }
}
