package moa.pay.exception;

import moa.global.exception.MoaException;
import moa.global.exception.MoaExceptionType;

public class TossPaymentException extends MoaException {

    private final TossPaymentExceptionType tossPaymentExceptionType;

    public TossPaymentException(TossPaymentExceptionType tossPaymentExceptionType) {
        this.tossPaymentExceptionType = tossPaymentExceptionType;
    }

    @Override
    public MoaExceptionType getExceptionType() {
        return tossPaymentExceptionType;
    }
}
