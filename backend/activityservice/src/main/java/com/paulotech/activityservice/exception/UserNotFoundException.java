package com.paulotech.activityservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("Invalid User: " + userId);
    }
}
