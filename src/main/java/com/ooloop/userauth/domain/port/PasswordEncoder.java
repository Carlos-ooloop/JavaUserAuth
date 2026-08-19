package com.ooloop.userauth.domain.port;

public interface PasswordEncoder {

    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
