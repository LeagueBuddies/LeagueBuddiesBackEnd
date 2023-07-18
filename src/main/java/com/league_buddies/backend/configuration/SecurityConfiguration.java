package com.league_buddies.backend.configuration;

import com.league_buddies.backend.security.authentication.AuthenticationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationFilter authenticationFilter;

    private final AuthenticationManager authenticationManager;

    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers(HttpMethod.GET, "/swagger-ui/index.html")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**")
                .permitAll()
                .anyRequest()
                .authenticated()

        )
                //TODO Find out why disabling CSRF makes it so that mockMvc needs a CSRF token?????????
                .csrf().disable()
                .authenticationProvider(authenticationProvider)
                .authenticationManager(authenticationManager)
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }
}
