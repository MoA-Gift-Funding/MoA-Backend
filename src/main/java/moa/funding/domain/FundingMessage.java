package moa.funding.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moa.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class FundingMessage {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_from_id")
    private Member from;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_to_id")
    private Member to;

    @Column
    private String content;

    @Column
    private boolean visible;

    public FundingMessage(Member from, Member to, String content, boolean visible) {
        this.from = from;
        this.to = to;
        this.content = content;
        this.visible = visible;
    }
}
