package com.payments.config.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = WithMockUserDetailsSecurityContextFactory.class)
public @interface WithMockUserDetails {
    long id() default 1L;
    String username() default "John@test.com";
    String role() default "ROLE_USER";
}