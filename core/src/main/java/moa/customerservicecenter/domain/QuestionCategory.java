package moa.customerservicecenter.domain;

import lombok.Getter;

@Getter
public enum QuestionCategory {

    CREATE_FUNDING("펀딩 개설"),
    PARTICIPATE_FUNDING("펀딩 참여"),
    DELIVERY("배송"),
    CANCEL_REFUND("취소/환불"),
    MEMBER("회원"),
    ETC("기타"),
    ;

    private final String koreanName;

    QuestionCategory(String koreanName) {
        this.koreanName = koreanName;
    }
}
