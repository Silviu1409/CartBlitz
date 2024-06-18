package com.savian.cartblitz.exception;

public class OrderInProgressException extends RuntimeException{
    public OrderInProgressException() {
        super("An order for the same customer is already in progress.");
    }
}
