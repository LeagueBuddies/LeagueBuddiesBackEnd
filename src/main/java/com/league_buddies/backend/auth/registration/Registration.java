package com.league_buddies.backend.auth.registration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Registration {
    private final String email;
    private final String password;
}
