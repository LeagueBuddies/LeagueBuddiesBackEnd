package com.league_buddies.backend.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
public class ApiException {
    private HttpStatus status;

    private String message;

    private ZonedDateTime timestamp;

    public ApiException(HttpStatus status,
                        String message,
                        ZonedDateTime zonedDateTime
    ) {
        this.status = status;
        this.message = message;
        this.timestamp = zonedDateTime;
    }

}
