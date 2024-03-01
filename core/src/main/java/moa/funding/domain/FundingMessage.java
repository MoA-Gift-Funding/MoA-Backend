package moa.funding.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static moa.funding.exception.FundingExceptionType.NO_AUTHORITY_FOR_FUNDING;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.funding.exception.FundingException;
import moa.global.domain.RootEntity;
import moa.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class FundingMessage extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_from_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_to_id", nullable = false)
    private Member receiver;

    @Column(nullable = false)
    private String content;

    @Enumerated(STRING)
    @Column(nullable = false)
    private MessageVisibility visible;

    public FundingMessage(Member sender, Member receiver, String content, MessageVisibility visible) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.visible = visible;
    }

    public void validateSender(Member member) {
        if (!this.sender.equals(member)) {
            throw new FundingException(NO_AUTHORITY_FOR_FUNDING);
        }
    }

    public void update(String content, MessageVisibility visible) {
        this.content = content;
        this.visible = visible;
    }
}
