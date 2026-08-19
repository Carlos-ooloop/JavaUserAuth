package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.PatchMyProfileCommand;
import com.ooloop.userauth.application.dto.UserResponse;
import com.ooloop.userauth.application.exceptions.UserAlreadyExistsException;
import com.ooloop.userauth.application.exceptions.UserNotFoundException;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;

public class PatchMyProfileCase {

    private final UserRepository userRepository;

    public PatchMyProfileCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse execute(String currentUsername, PatchMyProfileCommand command) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (command.username() != null && !command.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(command.username())) {
                throw new UserAlreadyExistsException(command.username());
            }
            user.setUsername(command.username());
        }

        if (command.email() != null && !command.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(command.email())) {
                throw new UserAlreadyExistsException(command.email());
            }
            user.setEmail(command.email());
        }

        User updated = userRepository.save(user);

        return new UserResponse(
                updated.getId(),
                updated.getUsername(),
                updated.getEmail(),
                updated.getRole().name(),
                updated.getCreatedAt()
        );
    }
}
