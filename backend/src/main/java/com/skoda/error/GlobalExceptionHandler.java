package com.skoda.error;

import jakarta.validation.ConstraintViolationException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.MessageFormat;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String VALIDATION_ERROR_TEMPLATE = "Field {0} - {1}";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        // collect all validation errors
        String validationErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(x -> MessageFormat.format(VALIDATION_ERROR_TEMPLATE, x.getField(), x.getDefaultMessage()))
                .collect(Collectors.joining());

        final ErrorResponse response = new ErrorResponse(validationErrors);
        log.error("Validation fails", ex);
        return new ResponseEntity<>(response, headers, status);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<Object> handle(ConstraintViolationException ex) {
        // collect all validation errors
        String validationErrors = ex.getConstraintViolations()
                .stream()
                .map(x -> MessageFormat.format(VALIDATION_ERROR_TEMPLATE, x.getPropertyPath(), x.getMessage()))
                .collect(Collectors.joining());

        final ErrorResponse response = new ErrorResponse(validationErrors);
        log.error("Validation fails", ex);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ApiException.class})
    ResponseEntity<ErrorResponse> handleApiException(ApiException apiException) {
        final ErrorResponse response = new ErrorResponse(apiException.getMessage());
        log.error("API call fails", apiException);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    ResponseEntity<ErrorResponse> handleUnexpectedError(RuntimeException ex) {
        final ErrorResponse response = new ErrorResponse(String.format("Unexpected server exception: %s", ex.getMessage()));
        log.error("Runtime fails", ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Value
    private static class ErrorResponse {
        String message;
    }

}