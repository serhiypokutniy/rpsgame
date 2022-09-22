package com.spsgame;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class MainControllerAdvice {

    /** Makes all exceptions look the same from outside */
    private static final String ERR = "Error processing request";

    /** Hides allowed methods */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<String> handle(HttpRequestMethodNotSupportedException exc) {
        log.warn("Requested method not allowed: {}", exc.getMessage());
        return new ResponseEntity<>(ERR, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<String> handle(ResponseStatusException exc) {
        log.error("Error processing request caused by {}", exc.getMessage(), exc);
        return new ResponseEntity<>(ERR, HttpStatus.BAD_REQUEST);
    }
}
