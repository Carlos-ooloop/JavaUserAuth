package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.LoginCommand;
import com.ooloop.userauth.application.dto.LoginResultCommand;
import com.ooloop.userauth.application.exceptions.InvalidCredentialsException;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.PasswordEncoder;
import com.ooloop.userauth.domain.port.TokenGenerator;
import com.ooloop.userauth.domain.port.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginUserCase {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public LoginUserCase(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    public LoginResultCommand execute(LoginCommand loginCommand){

        User user = userRepository.findByUsername(loginCommand.username()).orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches = passwordEncoder.matches(loginCommand.password(), user.getPassword());
        if(!passwordMatches){

            throw new InvalidCredentialsException();

        }
        String token = tokenGenerator.generate(user);

        return new LoginResultCommand(token);

    }


}
