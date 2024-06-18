package com.savian.cartblitz.exception;

public class CustomerEmailDuplicateException extends RuntimeException{
    public CustomerEmailDuplicateException() {
        super("A customer with the same email already exists.");
    }
}
