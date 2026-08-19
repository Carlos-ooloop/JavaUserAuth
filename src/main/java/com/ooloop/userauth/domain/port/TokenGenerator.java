package com.ooloop.userauth.domain.port;

import com.ooloop.userauth.domain.model.User;

public interface TokenGenerator {

    String generate(User user);
}
