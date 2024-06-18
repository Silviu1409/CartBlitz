package com.savian.cartblitz.exception;

public class TagNameDuplicateException extends RuntimeException{
    public TagNameDuplicateException() { super("A tag with the same name already exists."); }
}
