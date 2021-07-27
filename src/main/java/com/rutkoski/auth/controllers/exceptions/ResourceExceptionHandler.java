package com.rutkoski.auth.controllers.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResourceExceptionHandler {
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<StandardError> malformedJwtException(MalformedJwtException e, HttpServletRequest request){
        String error = "Not a valid token structure";
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError ex = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(ex);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<StandardError> expiredJwtException(ExpiredJwtException e, HttpServletRequest request){
        String error = "Session expired";
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError ex = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(ex);
    }
}
