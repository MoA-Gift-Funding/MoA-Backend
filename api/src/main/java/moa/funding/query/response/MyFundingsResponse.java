package moa.funding.query.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import moa.funding.domain.Funding;

public record MyFundingsResponse(
        List<MyFundingResponse> fundings
) {
    public record MyFundingResponse(
            @Schema(example = "1")
            Long id,

            @Schema(example = "https://펀딩-이미지-URL.com")
            String fundingImageUrl,

            @Schema(example = "나의 에어팟 펀딩")
            String title,

            @Schema(example = "2024-02-04")
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endDate,

            @Schema(description = "펀딩 달성 퍼센트", example = "56")
            int fundingRate,

            @Schema(description = "펀딩 상태", example = "진행중")
            String status,

            @Schema(description = "지금까지 펀딩된 금액", example = "50000")
            Long fundedAmount,

            @Schema(example = "17")
            Integer participationCount,

            @Schema(description = "상품 이미지", example = "https://imageurl.example")
            String productImageUrl
    ) {
        public static MyFundingResponse from(Funding funding) {
            return new MyFundingResponse(
                    funding.getId(),
                    funding.getImageUrl(),
                    funding.getTitle(),
                    funding.getEndDate(),
                    funding.getFundingRate(),
                    funding.getStatus().getDescription(),
                    funding.getFundedAmount().longValue(),
                    funding.getParticipants().size(),
                    funding.getProduct().getImageUrl()
            );
        }
    }

    public static MyFundingsResponse from(List<Funding> fundings) {
        return new MyFundingsResponse(
                fundings.stream()
                        .map(MyFundingResponse::from)
                        .toList()
        );
    }
}
