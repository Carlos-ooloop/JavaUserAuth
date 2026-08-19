package com.ooloop.userauth.infraestructure.config;

import com.ooloop.userauth.application.usecase.*;
import com.ooloop.userauth.domain.port.PasswordEncoder;
import com.ooloop.userauth.domain.port.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public RegisterUserCase registerUserCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new RegisterUserCase(userRepository, passwordEncoder);
    }

    @Bean
    public LoginUserCase loginUserCase(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                        com.ooloop.userauth.domain.port.TokenGenerator tokenGenerator) {
        return new LoginUserCase(userRepository, passwordEncoder, tokenGenerator);
    }

    @Bean
    public GetAllUsersCase getAllUsersCase(UserRepository userRepository) {
        return new GetAllUsersCase(userRepository);
    }

    @Bean
    public GetUserByIdCase getUserByIdCase(UserRepository userRepository) {
        return new GetUserByIdCase(userRepository);
    }

    @Bean
    public PatchUserCase patchUserCase(UserRepository userRepository) {
        return new PatchUserCase(userRepository);
    }

    @Bean
    public PatchMyProfileCase patchMyProfileCase(UserRepository userRepository) {
        return new PatchMyProfileCase(userRepository);
    }

    @Bean
    public PromoteToAdminCase promoteToAdminCase(UserRepository userRepository) {
        return new PromoteToAdminCase(userRepository);
    }

    @Bean
    public RemoveAdminCase removeAdminCase(UserRepository userRepository) {
        return new RemoveAdminCase(userRepository);
    }

    @Bean
    public SoftDeleteUserCase softDeleteUserCase(UserRepository userRepository) {
        return new SoftDeleteUserCase(userRepository);
    }
}
