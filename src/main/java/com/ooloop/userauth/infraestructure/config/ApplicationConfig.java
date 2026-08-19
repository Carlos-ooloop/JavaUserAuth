package com.ooloop.userauth.infraestructure.config;


import com.ooloop.userauth.application.usecase.LoginUserCase;
import com.ooloop.userauth.application.usecase.RegisterUserCase;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.PasswordEncoder;
import com.ooloop.userauth.domain.port.TokenGenerator;
import com.ooloop.userauth.domain.port.UserRepository;
import com.ooloop.userauth.infraestructure.security.JwtTokenGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationConfig {

    @Bean
    public RegisterUserCase registerUserCase(UserRepository userRepository, PasswordEncoder passwordEncoder){

        return new RegisterUserCase(userRepository, passwordEncoder);
    }
    @Bean
    public LoginUserCase loginUserCase(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenGenerator jwtTokenGenerator){

        return new LoginUserCase(userRepository,passwordEncoder, jwtTokenGenerator);
    }

}
