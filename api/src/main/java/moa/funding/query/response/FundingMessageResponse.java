package moa.funding.query.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import moa.friend.domain.Friend;
import moa.funding.domain.FundingMessage;
import moa.funding.domain.FundingParticipant;
import moa.member.domain.Member;

public record FundingMessageResponse(
        @Schema(description = "펀딩 ID")
        Long fundingId,

        @Schema(description = "펀딩 제목", example = "나의 에어팟 펀딩")
        String fundingTitle,

        @Schema(example = "https://user-image.com")
        String profileImageUrl,

        @Schema(example = "크왕이")
        String nickName,

        @Schema(example = "1")
        Long messageId,

        @Schema(example = "잘먹고 잘 살아라 즐거웠다...")
        String message,

        @Schema(description = "메시지 작성자 ID", example = "1")
        Long memberId,

        @Schema(description = "생성일자", example = "2024-01-13 12:00:34")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdDate
) {
    public static FundingMessageResponse of(FundingParticipant participant, List<Friend> friends) {
        FundingMessage message = participant.getFundingMessage();
        Member sender = message.getSender();
        String nickName = friends.stream()
                .filter(friend -> friend.getTarget().equals(message.getSender()))
                .findAny()
                .map(Friend::getNickname)
                .orElseGet(sender::getNickname);
        return new FundingMessageResponse(
                participant.getFunding().getId(),
                participant.getFunding().getTitle(),
                sender.getProfileImageUrl(),
                nickName,
                message.getId(),
                message.getContent(),
                sender.getId(),
                message.getCreatedDate()
        );
    }
}
