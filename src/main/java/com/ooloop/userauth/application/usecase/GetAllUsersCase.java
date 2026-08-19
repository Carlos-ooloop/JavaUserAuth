package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.UserListResponse;
import com.ooloop.userauth.application.dto.UserResponse;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;

import java.util.List;

public class GetAllUsersCase {

    private final UserRepository userRepository;

    public GetAllUsersCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserListResponse execute() {
        List<User> users = userRepository.findAllActive();

        List<UserResponse> response = users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getCreatedAt()
                ))
                .toList();

        return new UserListResponse(response, response.size());
    }
}
