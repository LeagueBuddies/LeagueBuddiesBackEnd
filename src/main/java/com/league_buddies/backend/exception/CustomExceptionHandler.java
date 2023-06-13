package com.league_buddies.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = {PlayerNotFoundException.class})
    public ResponseEntity<Object> handlePlayerNotFoundException
            (PlayerNotFoundException exception) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(status, exception.getMessage(), ZonedDateTime.now(ZoneId.of("Z")));
        return ResponseEntity.status(status).body(apiException);
    }
}
