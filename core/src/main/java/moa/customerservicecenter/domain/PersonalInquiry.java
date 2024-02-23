package moa.customerservicecenter.domain;

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
public class PersonalInquiry extends RootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    @Column(nullable = false)
    private QuestionCategory category;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String answer;

    public PersonalInquiry(QuestionCategory category, String content, Member member) {
        this.category = category;
        this.content = content;
        this.member = member;
    }
}
