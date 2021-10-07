package com.acme.testcommons.security;

import org.springframework.security.test.context.support.WithUserDetails;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithUserDetails("client@acme.com")
public @interface WithMockClient {
}
