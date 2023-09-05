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
		String accessToken = jwtProvider.getAccessTokenInCookie(request);

		if (accessToken != null) {
			if (!jwtProvider.validateToken(accessToken)) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 AccessToken 입니다");
			}

			if (redisDao.isEqualsBlackList(accessToken)) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이미 로그아웃된 AccessToken 입니다");
			}

			String refreshPointer = jwtProvider.getClaims(accessToken).getSubject();
			String refreshToken = redisDao.getValue(refreshPointer);
			if (refreshToken == null) {
				redisDao.setBlackList(accessToken);
				deleteCookies(response);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RefreshToken is different");
			}

			createAuthentication(jwtProvider.getTokenInUserId(refreshToken));
		}

		filterChain.doFilter(request, response);
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
