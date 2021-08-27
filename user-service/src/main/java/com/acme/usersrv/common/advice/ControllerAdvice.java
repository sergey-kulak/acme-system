package com.acme.usersrv.common.advice;

import com.acme.commons.advice.BaseControllerAdvice;
import com.acme.usersrv.company.exception.DuplicateCompanyException;
import com.acme.commons.exception.IllegalStatusChange;
import com.acme.usersrv.user.exception.DuplicateUserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice extends BaseControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<Object> handle(DuplicateCompanyException ex) {
        return createConflictResponse(ex);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(DuplicateUserException ex) {
        return createConflictResponse(ex);
    }
}
