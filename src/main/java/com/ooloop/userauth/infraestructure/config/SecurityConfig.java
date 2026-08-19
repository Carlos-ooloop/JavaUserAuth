package com.ooloop.userauth.infraestructure.config;

import com.ooloop.userauth.domain.port.PasswordEncoder;
import com.ooloop.userauth.infraestructure.security.BCryptPasswordEncoderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder springPasswordEncoder(){

        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoder passwordEncoder(org.springframework.security.crypto.password.PasswordEncoder encoder){

        return new BCryptPasswordEncoderAdapter(encoder);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth ->auth.requestMatchers("/auth/register").permitAll().anyRequest().authenticated());
        return http.build();

    }

}
