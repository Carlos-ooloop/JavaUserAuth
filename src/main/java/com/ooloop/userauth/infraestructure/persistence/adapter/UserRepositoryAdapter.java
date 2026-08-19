package com.ooloop.userauth.infraestructure.persistence.adapter;

import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;
import com.ooloop.userauth.infraestructure.persistence.entity.UserJpaEntity;
import com.ooloop.userauth.infraestructure.persistence.mappers.UserPersistenceMapper;
import com.ooloop.userauth.infraestructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository repository;


    public UserRepositoryAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save (User user){
        UserJpaEntity entity = UserPersistenceMapper.toEntity(user);

        UserJpaEntity saved = repository.save(entity);


        return UserPersistenceMapper.toDomain(repository.save(entity));


    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByID(Long id) {
        return repository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(UserPersistenceMapper::toDomain);
    }

}



