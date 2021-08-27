package com.acme.commons.exception;

public class IllegalStatusChange extends RuntimeException {
    public IllegalStatusChange() {
        super("Not allowed status change");
    }
}
