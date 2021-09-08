package com.acme.commons.advice;

import com.acme.commons.dto.ValidationErrorDto;
import com.acme.commons.exception.EntityNotFoundException;
import com.acme.commons.exception.IllegalStatusChange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

public class BaseControllerAdvice {
    @ExceptionHandler
    public ResponseEntity<Object> handle(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(new ValidationErrorDto(ex));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(EntityNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }


    protected ResponseEntity<Object> createConflictResponse(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    protected ResponseEntity<Object> createBadResponse(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(IllegalStatusChange ex) {
        return createBadResponse(ex);
    }

}
