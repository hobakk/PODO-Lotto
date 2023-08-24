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
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.example.sixnumber.global.exception.BlackListException;
import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.RedisDao;

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
		String token = jwtProvider.resolveToken(request);
		String refreshTokenInCookie = jwtProvider.getTokenValueInCookie(request ,"refreshToken");

		if (token != null) {
			try {
				if (!jwtProvider.filterChainValidateToken(token))
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다");
				if (redisDao.isEqualsBlackList(token))
					throw new BlackListException(null, jwtProvider.getClaims(token), "Blacked");

				String refreshPointer = jwtProvider.getClaims(token).getSubject();
				String refreshTokenInRedis = redisDao.getValue(refreshPointer);
				if (!refreshTokenInRedis.equals(refreshTokenInCookie) || jwtProvider.isTokenExpired(refreshTokenInRedis)) {
					redisDao.setBlackList(token);
					throw new OverlapException("중복 로그인 입니다");
				}

				createAuthentication(jwtProvider.getTokenInUserId(refreshTokenInRedis));
			} catch (ExpiredJwtException e) {
				String refreshPointer = e.getClaims().getSubject();
				if (inValidRefreshInCookie(refreshTokenInCookie, refreshPointer, response)) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 RefreshToken");
				}
			} catch (BlackListException e) {
				String refreshPointer = e.getClaims().getSubject();
				if (refreshTokenInCookie != null) {
					if (inValidRefreshInCookie(refreshTokenInCookie, refreshPointer, response)) {
						throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 RefreshToken");
					}
				} else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 RefreshTokenInCookie");
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean inValidRefreshInCookie(String refreshTokenInCookie, String refreshPointer,
		HttpServletResponse response) {

		String inRedisValue = redisDao.getValue(refreshPointer);
		if (!refreshTokenInCookie.equals(inRedisValue)) return true;

		String newAccessToken = jwtProvider.accessToken(refreshPointer);
		updateAccessTokenCookie(response, newAccessToken);
		createAuthentication(jwtProvider.getTokenInUserId(inRedisValue));
		return false;
	}

	private void updateAccessTokenCookie(HttpServletResponse response, String newAccessToken) {
		Cookie cookie = new Cookie("accessToken", newAccessToken);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	private void createAuthentication(Long userId) {
		UserDetails user = userDetailsService.loadUserById(userId);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}
