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
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.RedisDao;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

	private final UserDetailsServiceImpl userDetailsService;
	private final JwtProvider jwtProvider;
	private final RedisDao redisDao;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String token = jwtProvider.resolveToken(request, "access");

		if (token != null) {
			if (!jwtProvider.validateToken(token)) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다");
			}

			Long id = jwtProvider.getClaims(token).get("id", Long.class);
			if (!redisDao.checkKey(id)) {
				redisDao.deleteValues(id);
				throw new OverlapException("중복 로그인 입니다");
			}

			createAuthentication(id);
		}

		filterChain.doFilter(request, response);
	}

	private void createAuthentication(Long userId) {
		UserDetails user = userDetailsService.loadUserById(userId);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}
