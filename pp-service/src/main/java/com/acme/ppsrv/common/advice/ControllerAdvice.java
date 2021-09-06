package com.acme.ppsrv.common.advice;

import com.acme.commons.advice.BaseControllerAdvice;
import com.acme.ppsrv.publicpoint.exception.PlanNotAssignedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice extends BaseControllerAdvice {
    @ExceptionHandler
    public ResponseEntity<Object> handle(PlanNotAssignedException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
