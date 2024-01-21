package moa.member.exception;

import moa.global.exception.MoaException;
import moa.global.exception.MoaExceptionType;

public class MemberException extends MoaException {

    private final MemberExceptionType memberExceptionType;

    public MemberException(MemberExceptionType memberExceptionType) {
        this.memberExceptionType = memberExceptionType;
    }

    @Override
    public MoaExceptionType getExceptionType() {
        return memberExceptionType;
    }
}
