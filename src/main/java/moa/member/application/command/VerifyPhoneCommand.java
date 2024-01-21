package moa.member.application.command;

public record VerifyPhoneCommand(
        Long memberId,
        String verificationNumber
) {
}
