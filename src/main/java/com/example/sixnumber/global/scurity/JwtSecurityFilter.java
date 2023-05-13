package com.example.sixnumber.global.scurity;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

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

import io.jsonwebtoken.Claims;
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
		String token = jwtProvider.resolveToken(request);

		try {
			if (token != null) {
				if (!jwtProvider.validateToken(token)) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다");
				}

				Long id = jwtProvider.getClaims(token).get("id", Long.class);
				if (redisTemplate.opsForValue().get("RT: " + id) == null) {
					jwtProvider.setExpire(token);
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다");
				}

				Claims claims = jwtProvider.getClaims(token);
				createAuthentication(claims.getSubject());
			}
		} catch (ExpiredJwtException e) {
			Long id = e.getClaims().get("id", Long.class);
			String refreshTokenInRedis = redisTemplate.opsForValue().get("RT: " + id);

			if (Objects.isNull(refreshTokenInRedis) || jwtProvider.isTokenExpired(refreshTokenInRedis)) {
				redisTemplate.delete("RT: " + id);
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다");
			}

			String email = e.getClaims().getSubject();
			String newAccessToken = jwtProvider.accessToken(email, id);

			response.setHeader(JwtProvider.AUTHORIZATION_HEADER, "Bearer " + newAccessToken);
			createAuthentication(email);
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
