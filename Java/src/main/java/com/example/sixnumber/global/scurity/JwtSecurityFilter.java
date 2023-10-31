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

import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.global.exception.RefreshTokenIsNullException;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.RedisDao;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {
	private final UserDetailsServiceImpl userDetailsService;
	private final JwtProvider jwtProvider;
	private final RedisDao redisDao;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		TokenDto tokenDto = jwtProvider.resolveTokens(request);
		String accessToken = tokenDto.getAccessToken();
		String refreshToken = tokenDto.getRefreshToken();

		if (tokensIsNotNull(tokenDto)) {
			try {
				String verifiedAccessToken = validateAccessToken(accessToken);
				String refreshPointer = jwtProvider.getClaims(verifiedAccessToken).getSubject();

				redisDao.getValue(RedisDao.RT_KEY + refreshPointer)
					.ifPresentOrElse(
						value -> {
							validateRefreshToken(response, refreshToken, value);
							createAuthentication(jwtProvider.getTokenInUserId(value));
						},
						() -> {
							redisDao.setBlackList(verifiedAccessToken, (long) 300000);
							deleteCookieAndThrowException(response);
						}
					);
			} catch (ExpiredJwtException e) {
				String refreshPointer = e.getClaims().getSubject();
				redisDao.getValue(RedisDao.RT_KEY + refreshPointer)
					.ifPresentOrElse(
						value -> {
							validateRefreshToken(response, refreshToken, value);
							Claims claims = jwtProvider.getClaims(value);
							String newAccessToken = jwtProvider.accessToken(claims.get("key", String.class));
							int remainingSeconds = (int) Math.floor((double) jwtProvider.getRemainingTime(value) / 1000);
							if (remainingSeconds < 360) deleteCookieAndThrowException(response);

							jwtProvider.createCookieForAddHeaders(
								response, JwtProvider.ACCESS_TOKEN, newAccessToken, remainingSeconds);
							createAuthentication(claims.get("id", Long.class));
						},
						() -> deleteCookieAndThrowException(response)
					);
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean tokensIsNotNull(TokenDto tokenDto) {
		return tokenDto.getAccessToken() != null && tokenDto.getRefreshToken() != null;
	}

	private String validateAccessToken(String accessToken) {
		if (!jwtProvider.validateToken(accessToken))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 AccessToken 입니다");
		if (redisDao.isEqualsBlackList(accessToken))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이미 로그아웃된 AccessToken 입니다");

		return accessToken;
	}

	private void validateRefreshToken(HttpServletResponse response, String refreshToken, String refreshTokenInRedis) {
		if (!refreshToken.equals(refreshTokenInRedis)) deleteCookieAndThrowException(response);
	}

	private void deleteCookieAndThrowException(HttpServletResponse response) {
		deleteCookies(response, JwtProvider.ACCESS_TOKEN);
		deleteCookies(response, JwtProvider.REFRESH_TOKEN);
		throw new RefreshTokenIsNullException();
	}

	private void deleteCookies(HttpServletResponse response, String name) {
		jwtProvider.createCookieForAddHeaders(response, name, null, 0);
	}

	private void createAuthentication(Long userId) {
		UserDetails user = userDetailsService.loadUserById(userId);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}
