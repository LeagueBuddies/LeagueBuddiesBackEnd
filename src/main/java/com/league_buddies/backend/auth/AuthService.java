package com.league_buddies.backend.auth;

import com.league_buddies.backend.auth.registration.Registration;
import com.league_buddies.backend.security.jwt.JwtService;
import com.league_buddies.backend.user.User;
import com.league_buddies.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    public AuthResponse register(Registration registration) {
        User newUser = userService.createUser(new User(registration.getEmail(), registration.getPassword()));
        String token = jwtService.generateToken(newUser.getEmailAddress());
        return new AuthResponse(token);
    }
}
