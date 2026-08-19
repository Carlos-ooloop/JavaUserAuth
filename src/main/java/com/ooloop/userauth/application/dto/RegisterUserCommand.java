package com.ooloop.userauth.application.dto;

public record RegisterUserCommand(String email,String username, String password) {
}
