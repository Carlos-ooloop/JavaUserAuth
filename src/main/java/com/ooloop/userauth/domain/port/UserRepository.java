package com.ooloop.userauth.domain.port;

import com.ooloop.userauth.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    User save( User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByID ( Long id);
    boolean existsByEmail(String email);

}
