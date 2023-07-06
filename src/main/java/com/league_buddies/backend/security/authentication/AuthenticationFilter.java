package com.league_buddies.backend.security.authentication;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader("Authorization");
        final String token;
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("Null or wrong token.");
            filterChain.doFilter(request, response);
            return;
        }
        // JWT tokens start with "Bearer ". We have to remove that part to get the actual token.
        token = authorization.substring("Bearer ".length());
        System.out.println("Token: " + token);
        // TODO here is where we have to make sure the token is valid, for the right user, give the user authentication etc.

        filterChain.doFilter(request, response);
    }
}
