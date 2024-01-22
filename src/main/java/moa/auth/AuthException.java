package moa.auth;

import moa.global.exception.MoaException;
import moa.global.exception.MoaExceptionType;

public class AuthException extends MoaException {

    private final AuthExceptionType authExceptionType;

    public AuthException(AuthExceptionType authExceptionType) {
        this.authExceptionType = authExceptionType;
    }

    @Override
    public MoaExceptionType getExceptionType() {
        return authExceptionType;
    }
}
