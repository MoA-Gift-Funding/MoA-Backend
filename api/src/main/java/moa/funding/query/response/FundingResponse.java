package moa.funding.query.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import moa.friend.domain.Friend;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingStatus;

public record FundingResponse(
        @Schema(example = "3")
        Long fundingId,

        @Schema(example = "https://펀딩-이미지-URL.com")
        String fundingImageUrl,

        @Schema(example = "주노 하와이 보내주기")
        String title,

        @Schema(description = "생성일자", example = "2024-01-13 12:00:34")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdDate,

        @Schema(example = "2024-02-21")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endDate,

        @Schema(description = "펀딩 달성 퍼센트", example = "56")
        int fundingRate,

        @Schema(description = "펀딩 상태", example = "PROCESSING")
        FundingStatus status,

        @Schema(example = "1")
        Long memberId,

        @Schema(example = "최준호 (19학번)")
        String nickName,

        @Schema(example = "https://example.url")
        String profileImageUrl,

        @Schema(example = "1")
        Long productId,

        @Schema(example = "https://example.url")
        String productImageUrl
) {
    public static FundingResponse of(Funding funding, List<Friend> friends) {
        String fundingMemberNickname = friends.stream()
                .filter(friend -> friend.getTarget().equals(funding.getMember()))
                .findAny()
                .map(Friend::getNickname)
                .orElseGet(() -> funding.getMember().getNickname());
        return new FundingResponse(
                funding.getId(),
                funding.getImageUrl(),
                funding.getTitle(),
                funding.getCreatedDate(),
                funding.getEndDate(),
                funding.getFundingRate(),
                funding.getStatus(),
                funding.getMember().getId(),
                fundingMemberNickname,
                funding.getMember().getProfileImageUrl(),
                funding.getProduct().getId(),
                funding.getProduct().getImageUrl()
        );
    }
}
