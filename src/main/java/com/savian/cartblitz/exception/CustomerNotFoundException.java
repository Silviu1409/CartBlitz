package com.savian.cartblitz.exception;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException(long customerId) {
        super("Customer with id " + customerId + " doesn't exist in the db.");
    }

    public CustomerNotFoundException(String username) {
        super("Customer with username " + username + " doesn't exist in the db.");
    }
}
