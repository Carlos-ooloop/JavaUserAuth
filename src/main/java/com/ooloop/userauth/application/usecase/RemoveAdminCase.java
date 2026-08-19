package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.UserResponse;
import com.ooloop.userauth.application.exceptions.AccessDeniedOperationException;
import com.ooloop.userauth.application.exceptions.UserNotFoundException;
import com.ooloop.userauth.domain.model.Role;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;

public class RemoveAdminCase {

    private final UserRepository userRepository;

    public RemoveAdminCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse execute(Long id, String currentAdminUsername) {
        User user = userRepository.findByID(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getUsername().equals(currentAdminUsername)) {
            throw new AccessDeniedOperationException("An admin cannot remove their own admin privileges");
        }

        user.setRole(Role.USER);

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
