package com.savian.cartblitz.exception;

public class CustomerUsernameDuplicateException extends RuntimeException{
    public CustomerUsernameDuplicateException() {
        super("A customer with the same username already exists.");
    }
}
