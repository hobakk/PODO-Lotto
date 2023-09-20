package com.example.sixnumber.global.scurity;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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

import com.example.sixnumber.global.exception.AccessTokenIsExpiredException;
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
	private final PasswordEncoder encoder;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String accessToken = jwtProvider.getAccessTokenInCookie(request);
		String encodedRefreshToken = jwtProvider.resolveToken(request);

		if (encodedRefreshToken != null) {
			try {
				validateAccessToken(accessToken);
			} catch (ExpiredJwtException e) {
				String refreshPointer = e.getClaims().getSubject();
				String refreshToken = redisDao.getValue(RedisDao.RT_KEY, refreshPointer);
				if (refreshToken == null || !encoder.matches(refreshToken, encodedRefreshToken)) {
					deleteCookieAndThrowException(response);
				}

				Claims claims = jwtProvider.getClaims(refreshToken);
				String newAccessToken = jwtProvider.accessToken(claims.get("key", String.class));
				long remainingSeconds = Math.max(jwtProvider.getRemainingTime(refreshToken) /1000, 0);
				if (remainingSeconds < 360) deleteCookieAndThrowException(response);

				Cookie cookie = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, newAccessToken, remainingSeconds);
				response.addCookie(cookie);
				createAuthentication(claims.get("id", Long.class));
			}
		} else {
			if (accessToken != null) {
				try {
					String verifiedAccessToken = validateAccessToken(accessToken);
					String refreshPointer = jwtProvider.getClaims(verifiedAccessToken).getSubject();
					String refreshToken = redisDao.getValue(RedisDao.RT_KEY, refreshPointer);
					if (refreshToken == null) {
						redisDao.setBlackList(verifiedAccessToken);
						deleteCookieAndThrowException(response);
					}

					createAuthentication(jwtProvider.getTokenInUserId(refreshToken));
				} catch (ExpiredJwtException e) {
					throw new AccessTokenIsExpiredException();
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private String validateAccessToken(String accessToken) {
		if (!jwtProvider.validateToken(accessToken)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 AccessToken 입니다");
		}
		if (redisDao.isEqualsBlackList(accessToken)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이미 로그아웃된 AccessToken 입니다");
		}

		return accessToken;
	}

	private void deleteCookieAndThrowException(HttpServletResponse response) {
		deleteCookies(response);
		throw new RefreshTokenIsNullException();
	}

	private void deleteCookies(HttpServletResponse response) {
		Cookie access = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, null, 0);
		response.addCookie(access);
	}

	private void createAuthentication(Long userId) {
		UserDetails user = userDetailsService.loadUserById(userId);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}
