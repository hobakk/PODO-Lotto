package com.example.sixnumber.global.scurity;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.example.sixnumber.global.util.JwtProvider;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

	private final UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		if ((request.getRequestURI().equals("/api/users/signin") || request.getRequestURI().equals("/api/test/secured"))) {
			String token = JwtProvider.resolveToken(request);

			if (token != null) {
				if (!JwtProvider.validateToken(token)) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다");
				}
				Claims claims = JwtProvider.getClaims(token);
				createAuthentication(claims.getSubject());
			}
		}
		filterChain.doFilter(request, response);
	}

	private void createAuthentication(String email) {
		UserDetails user = userDetailsService.loadUserByUsername(email);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}
