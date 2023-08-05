package com.example.sixnumber.user.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {
	UserRole role() default UserRole.ROLE_USER;
	String username() default "testUser";
	String password() default "password";
	Status status() default Status.ACTIVE;
}