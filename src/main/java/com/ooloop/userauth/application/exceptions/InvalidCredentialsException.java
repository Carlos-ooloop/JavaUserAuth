package com.ooloop.userauth.application.exceptions;

public class InvalidCredentialsException extends RuntimeException{


    public InvalidCredentialsException(){
        super ("Invalid Credentials");
    }
}
