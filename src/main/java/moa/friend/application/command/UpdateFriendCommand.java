package moa.friend.application.command;

public record UpdateFriendCommand(
        Long memberId,
        Long friendId,
        String nickname
) {
}
