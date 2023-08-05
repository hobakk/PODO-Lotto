package com.example.sixnumber.user.controller;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

	@Override
	public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
		UserRole role = annotation.role();
		String username = annotation.username();
		Status status = annotation.status();
		String password = annotation.password();
		GrantedAuthority authority = new SimpleGrantedAuthority(role.toString());

		User user = new User(username, password, role, status);
		user.setId(99L);

		UsernamePasswordAuthenticationToken token =
			new UsernamePasswordAuthenticationToken(user, password, List.of(authority));
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(token);
		return context;
	}
}
