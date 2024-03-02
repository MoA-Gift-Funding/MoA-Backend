package moa.funding.query.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import moa.friend.domain.Friend;
import moa.funding.domain.Funding;
import moa.funding.domain.FundingParticipant;
import moa.funding.domain.FundingStatus;
import moa.funding.domain.ParticipantStatus;

public record ParticipatedFundingResponse(
        @Schema(example = "3")
        Long fundingId,

        @Schema(example = "https://펀딩-이미지-URL.com")
        String fundingImageUrl,

        @Schema(example = "주노 하와이 보내주기")
        String title,

        @Schema(example = "2024-02-21")
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endDate,

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
        String productImageUrl,

        @Schema(example = "참가자 id(회원 id가 아님, 참여 취소 시 사용)")
        Long fundingParticipantId,

        @Schema(example = "2024-02-20T12:24:01")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime participatedDate,

        @Schema(description = "펀딩한 금액", example = "20000")
        Long amount,

        @Schema(description = "참여 상태", example = "PARTICIPATING")
        ParticipantStatus participantStatus
) {
    public static ParticipatedFundingResponse of(FundingParticipant participant, List<Friend> friends) {
        Funding funding = participant.getFunding();
        String fundingMemberNickname = friends.stream()
                .filter(friend -> friend.getTarget().equals(funding.getMember()))
                .findAny()
                .map(Friend::getNickname)
                .orElseGet(() -> funding.getMember().getNickname());
        return new ParticipatedFundingResponse(
                funding.getId(),
                funding.getImageUrl(),
                funding.getTitle(),
                funding.getEndDate(),
                funding.getStatus(),
                funding.getMember().getId(),
                fundingMemberNickname,
                funding.getMember().getProfileImageUrl(),
                funding.getProduct().getId(),
                funding.getProduct().getImageUrl(),
                participant.getId(),
                participant.getCreatedDate(),
                participant.getAmount().longValue(),
                participant.getStatus()
        );
    }
}
