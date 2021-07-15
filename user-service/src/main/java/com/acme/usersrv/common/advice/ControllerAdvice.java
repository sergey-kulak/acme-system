package com.acme.usersrv.common.advice;

import com.acme.usersrv.common.dto.ValidationErrorDto;
import com.acme.usersrv.common.exception.EntityNotFoundException;
import com.acme.usersrv.company.exception.DuplicateCompanyException;
import com.acme.usersrv.company.exception.IllegalStatusChange;
import com.acme.usersrv.user.exception.DuplicateUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler
    public ResponseEntity<Object> handle(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(new ValidationErrorDto(ex));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(IllegalStatusChange ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(EntityNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(DuplicateCompanyException ex) {
        return createConflictResponse(ex);
    }

    private ResponseEntity<Object> createConflictResponse(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(DuplicateUserException ex) {
        return createConflictResponse(ex);
    }
}
