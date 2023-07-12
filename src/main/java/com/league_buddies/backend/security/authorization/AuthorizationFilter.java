package com.league_buddies.backend.security.authorization;

import jakarta.servlet.*;

import java.io.IOException;

public class AuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // TODO Implement custom logic for authorization if decide to have some.
        System.out.println("Authorization filter, request received: " + servletRequest);

        // Continue the filter chain
        filterChain.doFilter(servletRequest, servletResponse);

    }
}
