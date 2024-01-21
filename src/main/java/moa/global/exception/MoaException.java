package moa.global.exception;

public abstract class MoaException extends RuntimeException {

    protected MoaException() {
    }

    public abstract MoaExceptionType getExceptionType();
}
