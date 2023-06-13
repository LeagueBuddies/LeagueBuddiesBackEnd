package com.league_buddies.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
public class ApiException {
    private final HttpStatus status;

    private final String message;

    private final ZonedDateTime timestamp;

    public ApiException(HttpStatus status,
                        String message,
                        ZonedDateTime zonedDateTime
    ) {
        this.status = status;
        this.message = message;
        this.timestamp = zonedDateTime;
    }

}
