package moa.funding.exception;

import moa.global.exception.MoaException;
import moa.global.exception.MoaExceptionType;

public class FundingException extends MoaException {

    private final FundingExceptionType fundingExceptionType;

    public FundingException(FundingExceptionType fundingExceptionType) {
        this.fundingExceptionType = fundingExceptionType;
    }

    @Override
    public MoaExceptionType getExceptionType() {
        return fundingExceptionType;
    }
}
