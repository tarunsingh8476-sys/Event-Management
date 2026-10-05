package com.Backend.EventManagement.ExceptionHandler;

public class InvalidEventStateException extends RuntimeException {

    public  InvalidEventStateException(String message) {
        super(message);
    }
}
