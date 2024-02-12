package moa.member.application.command;

public record PhoneVerifyCommand(
        Long memberId,
        String verificationNumber
) {
}
