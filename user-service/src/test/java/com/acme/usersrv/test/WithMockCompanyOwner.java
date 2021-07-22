package com.acme.usersrv.test;

import org.springframework.security.test.context.support.WithUserDetails;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithUserDetails("company_owner@acme.com")
public @interface WithMockCompanyOwner {
}
