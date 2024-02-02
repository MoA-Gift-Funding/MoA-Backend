package moa.address.exception;

import moa.global.exception.MoaException;
import moa.global.exception.MoaExceptionType;

public class DeliveryAddressException extends MoaException {

    private final DeliveryAddressExceptionType deliveryAddressExceptionType;

    public DeliveryAddressException(DeliveryAddressExceptionType deliveryAddressExceptionType) {
        super(deliveryAddressExceptionType.name());
        this.deliveryAddressExceptionType = deliveryAddressExceptionType;
    }

    @Override
    public MoaExceptionType getExceptionType() {
        return deliveryAddressExceptionType;
    }
}
