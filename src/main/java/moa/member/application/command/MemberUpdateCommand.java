package moa.member.application.command;

public record MemberUpdateCommand(
        Long memberId,
        String nickname,
        String birthyear,
        String birthday,
        String profileImageUrl
) {
}
