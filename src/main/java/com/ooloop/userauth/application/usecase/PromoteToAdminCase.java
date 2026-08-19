package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.UserResponse;
import com.ooloop.userauth.application.exceptions.UserNotFoundException;
import com.ooloop.userauth.domain.model.Role;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;

public class PromoteToAdminCase {

    private final UserRepository userRepository;

    public PromoteToAdminCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse execute(Long id) {
        User user = userRepository.findByID(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setRole(Role.ADMIN);

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
