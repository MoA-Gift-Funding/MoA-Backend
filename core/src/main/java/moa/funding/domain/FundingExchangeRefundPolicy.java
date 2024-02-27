package moa.funding.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class FundingExchangeRefundPolicy {

    private static final List<Policy> cache = new ArrayList<>();
    private List<Policy> policies;

    static {
        cache.addAll(List.of(
                new Policy("결제 취소 안내", List.of(
                        "결제일로부터 7일 이내, 펀딩 종료 전까지 언제든 결제 취소 가능합니다.",
                        "취소는 마이페이지의 펀딩에서 신청할 수 있습니다."
                )),
                new Policy("환불 불가 유형", List.of(
                        "결제일로부터 7일이 지난 경우",
                        "펀딩이 종료되어 상품이 발송된 경우",
                        "펀딩 수증자의 귀책 사유로 인하여 상품이 멸실·훼손된 경우",
                        "상품의 사용 또는 소비로 인해 가치 등이 감소한 경우"
                ))
        ));
    }

    public FundingExchangeRefundPolicy() {
        this.policies = cache;
    }

    public record Policy(
            String title,
            List<String> content
    ) {
    }
}
