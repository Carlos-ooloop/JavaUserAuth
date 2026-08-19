package com.ooloop.userauth.infraestructure.security;

import com.ooloop.userauth.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenGenerator {

    private final SecretKey secretKey;
    private final long expiration;


    public JwtTokenGenerator(SecretKey secretKey, long expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }
    public JwtTokenGenerator (@Value("${jwt.secret}")String secret, @Value("${jwt.expiration}")long expiration){

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;

    }

    public String generate(User user){

        Date now = new Date();

        return Jwts.builder().subject(user.getUsername()).issuedAt(now).expiration(new Date(now.getTime()+ expiration)).signWith(secretKey).compact();

    }

    public String extractSubject(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();


    }
}
