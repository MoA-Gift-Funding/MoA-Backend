package moa.client.exception;

import moa.global.exception.MoaException;
import moa.global.exception.MoaExceptionType;

public class ExternalApiException extends MoaException {

    private final ExternalApiExceptionType externalApiExceptionType;

    public ExternalApiException(ExternalApiExceptionType externalApiExceptionType) {
        super(externalApiExceptionType.name());
        this.externalApiExceptionType = externalApiExceptionType;
    }

    @Override
    public MoaExceptionType getExceptionType() {
        return externalApiExceptionType;
    }
}
