package com.Backend.EventManagement.ExceptionHandler;

import com.Backend.EventManagement.DTO.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
                LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(EventNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(
                EventNotFoundException ex,
                HttpServletRequest request) {

            return build(
                    HttpStatus.NOT_FOUND,
                    "EVENT_NOT_FOUND",
                    ex.getMessage(),
                    request,
                    null
            );
        }

        @ExceptionHandler(DuplicateEventException.class)
        public ResponseEntity<ErrorResponse> handleDuplicate(
                DuplicateEventException ex,
                HttpServletRequest request) {

            return build(
                    HttpStatus.CONFLICT,
                    "DUPLICATE_EVENT",
                    ex.getMessage(),
                    request,
                    null
            );
        }
        @ExceptionHandler(InvalidEventStateException.class)
        public ResponseEntity<ErrorResponse> handleInvalidState(
                InvalidEventStateException ex,
                HttpServletRequest request) {

            return build(
                    HttpStatus.CONFLICT,
                    "INVALID_EVENT_STATE",
                    ex.getMessage(),
                    request,
                    null
            );
        }
        @ExceptionHandler(InvalidRequestException.class)
        public ResponseEntity<ErrorResponse> handleInvalidRequest(
                InvalidRequestException ex,
                HttpServletRequest request) {

            return build(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REQUEST",
                    ex.getMessage(),
                    request,
                    List.of(new com.Backend.EventManagement.DTO.ErrorResponse.FieldIssue(ex.getField(), ex.getMessage()))
            );
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(
                MethodArgumentNotValidException ex,
                HttpServletRequest request) {

            List<com.Backend.EventManagement.DTO.ErrorResponse.FieldIssue> details = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error ->
                            new com.Backend.EventManagement.DTO.ErrorResponse.FieldIssue(
                                    error.getField(),
                                    error.getDefaultMessage()
                            ))
                    .toList();

            return build(
                    HttpStatus.BAD_REQUEST,
                    "VALIDATION_FAILED",
                    "Request validation failed. See 'details' for each problem",
                    request,
                    details
            );
        }
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleUnexpected(
                Exception ex,
                HttpServletRequest request) {

            log.error(
                    "Unexpected error on {} {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex
            );

            return build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "INTERNAL_ERROR",
                    "Something went wrong on the server. Please try again later",
                    request,
                    null
            );
        }
        private ResponseEntity<ErrorResponse> build(
                HttpStatus status,
                String code,
                String message,
                HttpServletRequest request,
                List<com.Backend.EventManagement.DTO.ErrorResponse.FieldIssue> details) {

            ErrorResponse body = new ErrorResponse(
                    LocalDateTime.now(),
                    status.value(),
                    status.getReasonPhrase(),
                    code,
                    message,
                    request.getRequestURI(),
                    details
            );

            return ResponseEntity
                    .status(status)
                    .body(body);
        }

        private String describeType(Class<?> type) {

            if (type == null) {
                return "a valid value";
            }

            if (type == LocalDateTime.class) {
                return "a valid date-time like 2026-12-10T10:00:00";
            }

            if (type == Integer.class || type == int.class) {
                return "a whole number";
            }

            return "a value of type " + type.getSimpleName();
        }
    }

