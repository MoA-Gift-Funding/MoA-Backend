package moa.friend.request;

import io.swagger.v3.oas.annotations.media.Schema;
import moa.friend.application.command.UpdateFriendCommand;

public record UpdateFriendRequest(
        @Schema(example = "말랑")
        String nickname
) {
    public UpdateFriendCommand toCommand(Long memberId, Long friendId) {
        return new UpdateFriendCommand(
                memberId,
                friendId,
                nickname
        );
    }
}
