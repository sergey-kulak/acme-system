package com.acme.commons.security;

public class JwtClaims {
    public static final String SUBJECT = "sub";
    public static final String EXPIRATION_TIME = "exp";
    public static final String ISSUED_AT_CLAIM = "iat";
    public static final String ROLE = "role";
    public static final String COMPANY_ID = "cmpid";
    public static final String PUBLIC_POINT_ID = "ppid";
    public static final String ID = "id";

    private JwtClaims() {
    }
}
