package com.savian.cartblitz.exception;

public class ReviewNotFoundException extends RuntimeException{
    public ReviewNotFoundException(long reviewId) {
        super("Review with id " + reviewId + " doesn't exist in the db.");
    }
}
