package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.PatchUserCommand;
import com.ooloop.userauth.application.dto.UserResponse;
import com.ooloop.userauth.application.exceptions.UserNotFoundException;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;

public class PatchUserCase {

    private final UserRepository userRepository;

    public PatchUserCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse execute(Long id, PatchUserCommand command) {
        User user = userRepository.findByID(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (command.role() != null) {
            user.setRole(command.role());
        }
        if (command.enabled() != null) {
            user.setEnabled(command.enabled());
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
