package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.RegisterUserCommand;
import com.ooloop.userauth.application.dto.RegisterUserResponse;
import com.ooloop.userauth.application.exceptions.UserAlreadyExistsException;
import com.ooloop.userauth.domain.model.Role;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.PasswordEncoder;
import com.ooloop.userauth.domain.port.UserRepository;

import java.time.LocalDateTime;

public class RegisterUserCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterUserResponse execute(RegisterUserCommand command){

        if(userRepository.existsByEmail(command.email())){

            throw new UserAlreadyExistsException(command.email());
        }

        String encodedPassword = passwordEncoder.encode(command.password());

        User user = new User(null, command.username(), command.email(),encodedPassword, Role.USER, true, LocalDateTime.now());

        User saveduser = userRepository.save(user);

        return new RegisterUserResponse(saveduser.getId(), saveduser.getUsername(), saveduser.getEmail(), saveduser.getRole().name());
    }
}
