package com.wks.wikisearch.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<ErrorMessage> handleHttpClientErrorException(
            final HttpClientErrorException ex,
            final WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
                new Date(),
                "Client error occurred: " + ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException ex,
            final WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
                new Date(),
                "No valid input provided: " + ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleResourceNotFoundException(
            final ResourceNotFoundException ex,
            final WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND.value(),
                new Date(),
                "Object not found exception: " + ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DataAccessException.class})
    public ResponseEntity<ErrorMessage> handleDataAccessException(
            final DataAccessException ex,
            final WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                "Data access exception occurred: " + ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({HttpServerErrorException.class})
    public ResponseEntity<ErrorMessage> handleHttpServerErrorException(
            final Exception ex,
            final WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                "Server error occurred: " + ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public ResponseEntity<ErrorMessage> handleResourceAlreadyExistsException(
            final ResourceAlreadyExistsException ex,
            final WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT.value(),
                new Date(),
                "Resource already exists: " + ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }
}
