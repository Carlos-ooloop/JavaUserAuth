package com.ooloop.userauth.application.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String field) {
        super("User already exists with: " + field);
    }
}
