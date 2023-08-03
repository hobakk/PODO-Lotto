package com.example.sixnumber.user.controller;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.example.sixnumber.user.entity.User;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

	@Override
	public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
		String role = annotation.role();
		String username = annotation.username();
		GrantedAuthority authority = new SimpleGrantedAuthority(role);

		User user = new User(username, "password");

		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(user, "password", List.of(authority));
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(token);
		return context;
	}
}