package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidateException(final DataIntegrityViolationException e) {

        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage(),
                "conflict",
                LocalDateTime.now(),
                ErrorResponse.StatusEnum._409_CONFLICT));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final EntityNotFoundException e) {

        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(),
                "not found",
                LocalDateTime.now(),
                ErrorResponse.StatusEnum._404_NOT_FOUND));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleForbiddenException(final ForbiddenException e) {

        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(),
                "forbidden",
                LocalDateTime.now(),
                ErrorResponse.StatusEnum._400_BAD_REQUEST));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e) {

        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage(),
                "internal error",
                LocalDateTime.now(),
                ErrorResponse.StatusEnum._500_INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(final ValidationException e) {

        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(),
                "bad request",
                LocalDateTime.now(),
                ErrorResponse.StatusEnum._400_BAD_REQUEST));
    }

}