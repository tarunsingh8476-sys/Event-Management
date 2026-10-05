package com.Backend.EventManagement.ExceptionHandler;

public class DuplicateEventException extends RuntimeException {

    public DuplicateEventException(String name , String venue){
        super("An event named '" + name + "'at '" + venue + "' already exists with the same venue.");
    }
}
