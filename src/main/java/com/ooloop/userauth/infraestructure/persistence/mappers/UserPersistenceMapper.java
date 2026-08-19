package com.ooloop.userauth.infraestructure.persistence.mappers;

import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.infraestructure.persistence.entity.UserJpaEntity;

public class UserPersistenceMapper {
    private UserPersistenceMapper(){}
    public static User toDomain(UserJpaEntity entity){

        return new User(entity.getId(), entity.getUsername(), entity.getEmail(), entity.getPassword(), entity.getRole(), entity.isEnabled(), entity.getCreatedAt());


    }
    public static UserJpaEntity toEntity(User user){

        return new UserJpaEntity(user.getId(), user.getEmail(), user.getPassword(), user.isEnabled(), user.getRole(), user.getCreatedAt(), user.getUsername());
    }
}
