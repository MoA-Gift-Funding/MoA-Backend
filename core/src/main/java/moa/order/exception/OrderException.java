package moa.order.exception;

import moa.global.exception.MoaException;

public class OrderException extends MoaException {

    private final OrderExceptionType exceptionType;

    public OrderException(OrderExceptionType exceptionType) {
        super(exceptionType.name());
        this.exceptionType = exceptionType;
    }

    @Override
    public OrderExceptionType getExceptionType() {
        return exceptionType;
    }
}
