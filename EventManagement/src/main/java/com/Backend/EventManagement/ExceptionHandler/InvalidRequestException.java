package com.Backend.EventManagement.ExceptionHandler;

public class InvalidRequestException extends RuntimeException{
    private final String field;
    public InvalidRequestException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
