package com.league_buddies.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(
                status, exception.getMessage(), ZonedDateTime.now(ZoneId.of("Z"))
        );
        return ResponseEntity.status(status).body(apiException);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                status, exception.getMessage(), ZonedDateTime.now(ZoneId.of("Z"))
        );
        return ResponseEntity.status(status).body(apiException);
    }

    @ExceptionHandler(value = {UsernameAlreadyExistsException.class})
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException exception) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiException apiException = new ApiException(
                status, exception.getMessage(), ZonedDateTime.now(ZoneId.of("Z"))
        );
        return ResponseEntity.status(status).body(apiException);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<Object> handleMessageNotReadableException(HttpMessageNotReadableException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                status, exception.getMessage(), ZonedDateTime.now(ZoneId.of("Z"))
        );
        return ResponseEntity.status(status).body(apiException);
    }

    @ExceptionHandler(value = {InvalidPasswordException.class})
    public ResponseEntity<Object> handleMessageNotReadableException(InvalidPasswordException exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ApiException apiException = new ApiException(
                status, exception.getMessage(), ZonedDateTime.now(ZoneId.of("Z"))
        );
        return ResponseEntity.status(status).body(apiException);
    }
}
