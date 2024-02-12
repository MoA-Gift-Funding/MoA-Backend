package moa.member.application.command;

public record SignupCommand(
        Long memberId,
        String email,
        String nickname,
        String birthday,
        String birthyear,
        String profileImageUrl
) {
}
