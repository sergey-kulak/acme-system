package com.acme.usersrv.company.exception;

public class DuplicateCompanyException extends RuntimeException {
    private static final String MESSAGE = "Company with specified vatin, reg number or full name already registered";

    public DuplicateCompanyException() {
        super(MESSAGE);
    }
}
