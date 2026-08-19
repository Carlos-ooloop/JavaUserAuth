package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.exceptions.AccessDeniedOperationException;
import com.ooloop.userauth.application.exceptions.UserNotFoundException;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;

public class SoftDeleteUserCase {

    private final UserRepository userRepository;

    public SoftDeleteUserCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(Long id, String currentAdminUsername) {
        User user = userRepository.findByID(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getUsername().equals(currentAdminUsername)) {
            throw new AccessDeniedOperationException("An admin cannot delete their own account");
        }

        userRepository.softDelete(id);
    }
}
