package com.ooloop.userauth.application.exceptions;

public class UserAlreadyExistsException extends RuntimeException{

    public UserAlreadyExistsException(String email){

        super ("User already exists with email: " + email);
    }
}
