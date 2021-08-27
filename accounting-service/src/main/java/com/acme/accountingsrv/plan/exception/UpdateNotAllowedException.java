package com.acme.accountingsrv.plan.exception;

public class UpdateNotAllowedException extends RuntimeException {
    private static final String MESSAGE = "Only inactive plan can be modified";

    public UpdateNotAllowedException() {
        super(MESSAGE);
    }
}
