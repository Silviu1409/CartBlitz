package com.savian.cartblitz.exception;

public class ProductQuantityException extends RuntimeException{
    public ProductQuantityException(long productId, Integer productQuantity) {
        super("Product with id " + productId + " has only " + productQuantity + " items left in stock.");
    }
}
