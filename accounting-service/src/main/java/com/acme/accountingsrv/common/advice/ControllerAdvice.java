package com.acme.accountingsrv.common.advice;

import com.acme.accountingsrv.plan.exception.TableCountExceedLimitException;
import com.acme.accountingsrv.plan.exception.UpdateNotAllowedException;
import com.acme.commons.advice.BaseControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice extends BaseControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<Object> handle(UpdateNotAllowedException ex) {
        return createBadResponse(ex);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(TableCountExceedLimitException ex) {
        return createBadResponse(ex);
    }

}
