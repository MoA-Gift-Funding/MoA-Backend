package moa.delivery.exception;

import moa.global.exception.MoaException;
import moa.global.exception.MoaExceptionType;

public class DeliveryException extends MoaException {

    private final DeliveryExceptionType deliveryExceptionType;

    public DeliveryException(DeliveryExceptionType deliveryExceptionType) {
        this.deliveryExceptionType = deliveryExceptionType;
    }

    @Override
    public MoaExceptionType getExceptionType() {
        return deliveryExceptionType;
    }
}
