package com.savian.cartblitz.exception;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(long orderId) {
        super("Order with id " + orderId + " doesn't exist in the db.");
    }
}
