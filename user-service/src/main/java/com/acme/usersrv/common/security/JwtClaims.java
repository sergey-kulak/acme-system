package com.acme.usersrv.common.security;

public class JwtClaims {
    public static final String SUBJECT = "sub";
    public static final String EXPIRATION_TIME = "exp";
    public static final String ISSUED_AT_CLAIM = "iat";
    public static final String ROLE = "role";
    public static final String COMPANY_ID = "cmpid";

    private JwtClaims() {
    }
}
