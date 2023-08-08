package com.example.sixnumber.global.scurity;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.example.sixnumber.global.util.JwtProvider;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

	private final UserDetailsServiceImpl userDetailsService;
	private final JwtProvider jwtProvider;
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String token = jwtProvider.resolveToken(request, "access");

		try {
			if (token != null) {
				if (!jwtProvider.validateToken(token)) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다");
				}

				Long id = jwtProvider.getClaims(token).get("id", Long.class);
				if (redisTemplate.opsForValue().get("RT: " + id) == null) {
					jwtProvider.setExpire(token);
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 토큰 입니다");
				}

				createAuthentication(id);
			}
		} catch (ExpiredJwtException e) {
			String refreshToken = jwtProvider.resolveToken(request, "refresh");
			if (jwtProvider.validateRefreshToken(token, refreshToken))
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다");

			Long id = e.getClaims().get("id", Long.class);
			String email = e.getClaims().getSubject();
			String newAccessToken = jwtProvider.accessToken(email, id);

			response.setHeader(JwtProvider.AUTHORIZATION_HEADER, "Bearer " + newAccessToken);
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
