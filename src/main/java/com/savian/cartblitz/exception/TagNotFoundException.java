package com.savian.cartblitz.exception;

public class TagNotFoundException extends RuntimeException{
    public TagNotFoundException(long tagId) {
        super("Tag with id " + tagId + " doesn't exist in the db.");
    }

    public TagNotFoundException(String name) {
        super("Tag with name " + name + " doesn't exist in the db.");
    }
}
