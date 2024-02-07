package moa.funding.query.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import moa.friend.domain.Friend;
import moa.funding.domain.FundingMessage;
import moa.member.domain.Member;

public record FundingMessageResponse(
        @Schema(example = "https://user-image.com")
        String profileImageUrl,

        @Schema(example = "크왕이")
        String nickName,

        @Schema(example = "잘먹고 잘 살아라 즐거웠다...")
        String message,

        @Schema(description = "메시지 작성자 ID", example = "1")
        Long memberId,

        @Schema(description = "생성일자", example = "2024-01-13 12:00:34")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdDate
) {
    public static FundingMessageResponse of(FundingMessage message, List<Friend> friends) {
        Member sender = message.getSender();
        String nickName = friends.stream()
                .filter(friend -> Objects.equals(friend.getTarget().getId(), message.getSender().getId()))
                .findAny()
                .map(Friend::getNickname)
                .orElseGet(sender::getNickname);
        return new FundingMessageResponse(
                sender.getProfileImageUrl(),
                nickName,
                message.getContent(),
                sender.getId(),
                message.getCreatedDate()
        );
    }
}
