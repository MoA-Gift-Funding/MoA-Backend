package moa.notification.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    private String imageUrl;

    @Enumerated(STRING)
    @Column(nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private boolean isRead = false;

    public Notification(
            String url,
            String title,
            String message,
            String imageUrl,
            NotificationType type,
            Member member
    ) {
        this.url = url;
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
        this.member = member;
        this.type = type;
    }

    public void read() {
        this.isRead = true;
    }

    public String getType() {
        return type.name().toLowerCase();
    }
}
