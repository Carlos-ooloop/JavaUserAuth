package com.ooloop.userauth.infraestructure.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtTokenGenerator jwtTokenGenerator;

    public JwtAuthenticationFilter(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")){

            filterChain.doFilter(request,response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {

            String username = jwtTokenGenerator.extractSubject(token);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        catch (JwtException|IllegalArgumentException e){


            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request,response);

    }
}
