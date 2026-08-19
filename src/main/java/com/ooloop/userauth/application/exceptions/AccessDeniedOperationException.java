package com.ooloop.userauth.application.exceptions;

public class AccessDeniedOperationException extends RuntimeException {

    public AccessDeniedOperationException(String message) {
        super(message);
    }
}
