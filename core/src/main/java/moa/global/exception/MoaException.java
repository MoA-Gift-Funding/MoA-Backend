package moa.global.exception;

public abstract class MoaException extends RuntimeException {

    protected MoaException(MoaExceptionType exceptionType) {
        super("[%s]: %s".formatted(exceptionType.name(), exceptionType.getMessage()));
    }

    public abstract MoaExceptionType getExceptionType();
}
