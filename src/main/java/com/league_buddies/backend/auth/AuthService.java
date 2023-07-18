package com.league_buddies.backend.auth;

import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.exception.InvalidPasswordException;
import com.league_buddies.backend.exception.UserNotFoundException;
import com.league_buddies.backend.exception.UsernameAlreadyExistsException;
import com.league_buddies.backend.security.jwt.JwtService;
import com.league_buddies.backend.user.User;
import com.league_buddies.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

//    @Autowired
//    private AuthenticationManager authenticationManager;

    public AuthResponse register(AuthRequest authRequest) {
        String username = authRequest.username();
        String password = authRequest.password();

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Both a username and a password are needed.");
        }

        Optional<User> optionalUser = userRepository.findByEmailAddress(authRequest.username());
        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
        }

        User newUser = userRepository.save(new User(authRequest.username(), passwordEncoder.encode(authRequest.password())));
        String token = jwtService.generateToken(newUser.getEmailAddress());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest authRequest) {
        if (authRequest.username() == null ||
                authRequest.password() == null ||
                authRequest.username().trim().isEmpty() ||
                authRequest.password().trim().isEmpty()
        ) {
            throw new IllegalArgumentException("Both a username and a password are needed.");
        }
        Optional<User> optionalUser = userRepository.findByEmailAddress(authRequest.username());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("The user was not found using the email you've provided.");
        }
        User user = optionalUser.get();
        if (passwordEncoder.matches(authRequest.password(), user.getPassword())){
            //TODO Check if you need this because the application is working without it but it might be needed.
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            authRequest.username(), authRequest.password()
//                    )
//            );
            String token = jwtService.generateToken(user.getEmailAddress());
            return new AuthResponse(token);
        } else {
            // TODO Add a way to keep track of how many times they've tried and block them after a certain number of tries.
            throw new InvalidPasswordException("Password entered is incorrect.");
        }
    }
}
