package com.savian.cartblitz.exception;

public class OrderProductNotFoundException extends RuntimeException{
    public OrderProductNotFoundException(long orderId, long productId) {
        super("OrderProduct with order id " + orderId + " and product id " + productId + " doesn't exist in the db.");
    }
}
