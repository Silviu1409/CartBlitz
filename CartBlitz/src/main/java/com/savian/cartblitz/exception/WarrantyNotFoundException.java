package com.savian.cartblitz.exception;

public class WarrantyNotFoundException extends RuntimeException{
    public WarrantyNotFoundException(long warrantyId) {
        super("Warranty with id " + warrantyId + " doesn't exist in the db.");
    }
}
