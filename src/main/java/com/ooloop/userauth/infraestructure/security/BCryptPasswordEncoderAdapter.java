package com.ooloop.userauth.infraestructure.security;

import com.ooloop.userauth.domain.port.PasswordEncoder;

public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

   private final org.springframework.security.crypto.password.PasswordEncoder delegate;

    public BCryptPasswordEncoderAdapter(org.springframework.security.crypto.password.PasswordEncoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public String encode(String rawPassword){
        return delegate.encode(rawPassword);
    }
    @Override

    public boolean matches(String rawPassword,String encodePassword){

        return delegate.matches(rawPassword,encodePassword);
    }
}
