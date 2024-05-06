package com.savian.cartblitz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ CustomerNotFoundException.class})
    public ResponseEntity<String> handle(CustomerNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({ CustomerUsernameDuplicateException.class})
    public ResponseEntity<String> handle(CustomerUsernameDuplicateException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({ ReviewNotFoundException.class})
    public ResponseEntity<String> handle(ReviewNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({ ProductNotFoundException.class})
    public ResponseEntity<String> handle(ProductNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({ ProductQuantityException.class})
    public ResponseEntity<String> handle(ProductQuantityException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({ WarrantyNotFoundException.class})
    public ResponseEntity<String> handle(WarrantyNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({ OrderNotFoundException.class})
    public ResponseEntity<String> handle(OrderNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({ OrderInProgressException.class})
    public ResponseEntity<String> handle(OrderInProgressException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({ OrderProductNotFoundException.class})
    public ResponseEntity<String> handle(OrderProductNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({ TagNotFoundException.class})
    public ResponseEntity<String> handle(TagNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({ TagNameDuplicateException.class})
    public ResponseEntity<String> handle(TagNameDuplicateException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new LinkedHashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
