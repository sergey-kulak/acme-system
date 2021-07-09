package com.acme.usersrv.user.exception;

public class DuplicateUserException extends RuntimeException {
    private static final String MESSAGE = "User with specified email already exists";

    public DuplicateUserException() {
        super(MESSAGE);
    }
}
