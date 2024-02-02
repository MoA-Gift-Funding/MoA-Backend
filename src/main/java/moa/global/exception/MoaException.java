package moa.global.exception;

public abstract class MoaException extends RuntimeException {

    protected MoaException(String message) {
        super(message);
    }

    public abstract MoaExceptionType getExceptionType();
}
