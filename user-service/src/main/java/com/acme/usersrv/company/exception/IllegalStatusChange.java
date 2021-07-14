package com.acme.usersrv.company.exception;

public class IllegalStatusChange extends RuntimeException {
    public IllegalStatusChange() {
        super("Not allowed status change");
    }
}
