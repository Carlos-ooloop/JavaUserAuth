package com.ooloop.userauth.infraestructure.persistence.repository;

import com.ooloop.userauth.infraestructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.deletedAt IS NULL")
    List<UserJpaEntity> findAllActive();

    @Query("SELECT u FROM UserJpaEntity u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<UserJpaEntity> findActiveById(Long id);
}
