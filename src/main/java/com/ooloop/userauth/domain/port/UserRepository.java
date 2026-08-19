package com.ooloop.userauth.domain.port;

import com.ooloop.userauth.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByID(Long id);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findAllActive();
    void softDelete(Long id);
}
