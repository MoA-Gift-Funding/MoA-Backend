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
                new Policy("결제 취소 및 환불 안내", List.of(
                        "결제일로부터 7일 이내, 펀딩 종료 전까지 언제든 결제 취소 가능합니다.",
                        "취소는 마이페이지의 펀딩에서 신청할 수 있습니다."
                )),
                new Policy("환불 불가 유형", List.of(
                        "결제일로부터 7일이 지난 경우",
                        "펀딩 수증자의 귀책 사유로 인하여 상품이 멸실·훼손된 경우 (의류에 화장품 얼룩이 묻어있는 경우, 구성품의 누락, 밀봉 상품의 포장을 훼손한 경우 등)",
                        "상품의 사용 또는 소비로 인해 가치 등이 감소한 경우 (전자기기의 전력 연결 등 사용 흔적이 남아 있는 경우, 향수나 화장품 등을 사용한 경우 등)",
                        "신선·냉동식품, 식물 등 시간이 지남에 따라 재판매가 곤란할 정도로 가치가 떨어지는 상품인 경우",
                        "숙박권, 촬영권 등 사전 예약이 필요한 상품의 사용 기한이 임박하여 재판매가 어려운 경우",
                        "각인, 도장, 1:1 맞춤 제작 등 주문에 따라 개별적으로 생산되는 상품인 경우",
                        "전자 티겟(QR코드, 바코드 포함) 등 사실상 회수가 불가능하여 상품 공급 업체에게 중대한 피해가 예상되는 경우",
                        "전자책, CD, DVD, 소프트웨어 등 복제가 가능한 상품을 개시 및 열람한 경우",
                        "기타 법령 및 약관에 의해 리워드 반품이 제한되는 경우"
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
