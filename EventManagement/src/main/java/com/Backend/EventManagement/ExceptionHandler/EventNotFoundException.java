package com.Backend.EventManagement.ExceptionHandler;

public class EventNotFoundException extends RuntimeException{
    public EventNotFoundException(Long id){
        super("The event named '" + id + "' could not be found.");
    }
}
