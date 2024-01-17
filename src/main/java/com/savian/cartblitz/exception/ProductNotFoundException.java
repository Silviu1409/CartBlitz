package com.savian.cartblitz.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(long productId) {
        super("Product with id " + productId + " doesn't exist in the db.");
    }
}
