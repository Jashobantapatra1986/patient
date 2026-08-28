package com.dppm.patient.exceptions;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(PatientNotFoundException exception) { return ApiError.of(exception.getMessage()); }

    @ExceptionHandler({DuplicatePatientException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflict(DuplicatePatientException exception) { return ApiError.of(exception.getMessage()); }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError invalidArgument(IllegalArgumentException exception) { return ApiError.of(exception.getMessage()); }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validation(WebExchangeBindException exception) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
        return new ApiError(Instant.now(), "Validation failed", fields);
    }

    public record ApiError(Instant timestamp, String message, Map<String, String> fields) {
        static ApiError of(String message) { return new ApiError(Instant.now(), message, Map.of()); }
    }
}
