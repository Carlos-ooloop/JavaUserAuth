package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.UserResponse;
import com.ooloop.userauth.application.exceptions.UserNotFoundException;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;

public class GetUserByIdCase {

    private final UserRepository userRepository;

    public GetUserByIdCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse execute(Long id) {
        User user = userRepository.findByID(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return toResponse(user);
    }

    public UserResponse executeByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
