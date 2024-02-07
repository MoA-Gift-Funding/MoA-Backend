package moa.funding.query.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import moa.friend.domain.Friend;
import moa.funding.domain.Funding;

public record FundingResponse(
        @Schema(example = "3")
        Long fundingId,

        @Schema(example = "https://펀딩-이미지-URL.com")
        String fundingImageUrl,

        @Schema(example = "주노 하와이 보내주기")
        String title,

        @Schema(example = "2024-02-21")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endDate,

        @Schema(description = "펀딩 상태", example = "진행중")
        String status,

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
                .filter(friend -> Objects.equals(friend.getTarget().getId(), funding.getMember().getId()))
                .findAny()
                .map(Friend::getNickname)
                .orElseGet(() -> funding.getMember().getNickname());
        return new FundingResponse(
                funding.getId(),
                funding.getImageUrl(),
                funding.getTitle(),
                funding.getEndDate(),
                funding.getStatus().getDescription(),
                funding.getMember().getId(),
                fundingMemberNickname,
                funding.getMember().getProfileImageUrl(),
                funding.getProduct().getId(),
                funding.getProduct().getImageUrl()
        );
    }
}
