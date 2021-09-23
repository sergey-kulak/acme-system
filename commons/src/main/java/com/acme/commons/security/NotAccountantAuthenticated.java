package com.acme.commons.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER'," +
        "'CHEF','COOK','WAITER','CLIENT')")
public @interface NotAccountantAuthenticated {
}
