package com.ooloop.userauth.infraestructure.config;


import com.ooloop.userauth.application.usecase.RegisterUserCase;
import com.ooloop.userauth.domain.port.PasswordEncoder;
import com.ooloop.userauth.domain.port.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationConfig {

    @Bean
    public RegisterUserCase registerUserCase(UserRepository userRepository, PasswordEncoder passwordEncoder){

        return new RegisterUserCase(userRepository, passwordEncoder);
    }


}
