package moa.product.exception;

import moa.global.exception.MoaException;
import moa.global.exception.MoaExceptionType;

public class ProductException extends MoaException {

    private final ProductExceptionType productExceptionType;

    public ProductException(ProductExceptionType productExceptionType) {
        this.productExceptionType = productExceptionType;
    }

    @Override
    public MoaExceptionType getExceptionType() {
        return productExceptionType;
    }
}
