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
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.user.dto.CookiesResponse;

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
		CookiesResponse cookies = jwtProvider.getTokenValueInCookie(request);

		if (cookies.getAccessCookie() != null && cookies.getRefreshCookie() != null) {
			try {
				String accessToken = cookies.getAccessCookie().getValue();
				String refreshToken = cookies.getRefreshCookie().getValue();
				if (!jwtProvider.validateToken(accessToken)) throw new CustomException(ErrorCode.INVALID_TOKEN);

				if (redisDao.isEqualsBlackList(accessToken))
					throw new BlackListException(null, jwtProvider.getClaims(accessToken), "Blacked");

				String refreshPointer = jwtProvider.getClaims(accessToken).getSubject();
				// 중복 로그인에 대한 대처를 더 생각해 봐야함
				if (isDifferent(refreshToken, refreshPointer)) {
					redisDao.setBlackList(accessToken);
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RefreshToken is different");
				}

				createAuthentication(jwtProvider.getTokenInUserId(refreshToken));
			} catch (ExpiredJwtException e) {
				String refreshPointer = e.getClaims().getSubject();
				if (validateRefreshInCookie(cookies.getRefreshCookie().getValue(), refreshPointer, response)) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 RefreshToken");
				}
			} catch (BlackListException e) { // 더 깊게 고민해 봐야함
				String refreshPointer = e.getClaims().getSubject();
				if (validateRefreshInCookie(cookies.getRefreshCookie().getValue(), refreshPointer, response)) {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 RefreshToken");
				}
			} catch (CustomException e) {
				deleteCookies(response);
			}
		} else if (cookies.getAccessCookie() == null && cookies.getRefreshCookie() != null) {
			String refreshToken = cookies.getRefreshCookie().getValue();
			if (!jwtProvider.validateRefreshToken(refreshToken)) throw new CustomException(ErrorCode.INVALID_TOKEN);

			String refreshPointer = jwtProvider.getClaims(refreshToken).get("key", String.class);
			if (isDifferent(refreshToken, refreshPointer)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RefreshToken is different");
			}

			String newAccessToken = jwtProvider.accessToken(refreshPointer);
			updateAccessTokenCookie(response, newAccessToken);
			createAuthentication(jwtProvider.getTokenInUserId(refreshToken));
		}

		filterChain.doFilter(request, response);
	}

	private boolean validateRefreshInCookie(String refreshTokenInCookie, String refreshPointer,
		HttpServletResponse response) {

		if (isDifferent(refreshTokenInCookie, refreshPointer)) return true;

		String newAccessToken = jwtProvider.accessToken(refreshPointer);
		updateAccessTokenCookie(response, newAccessToken);
		createAuthentication(jwtProvider.getTokenInUserId(refreshTokenInCookie));
		return false;
	}

	private boolean isDifferent(String refreshTokenInCookie, String refreshPointer) {
		String inRedisValue = redisDao.getValue(refreshPointer);
		return !refreshTokenInCookie.equals(inRedisValue);
	}

	private void updateAccessTokenCookie(HttpServletResponse response, String newAccessToken) {
		Cookie cookie = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, newAccessToken, 300);
		response.addCookie(cookie);
	}

	private void deleteCookies(HttpServletResponse response) {
		Cookie access = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, null, 0);
		Cookie refresh = jwtProvider.createCookie(JwtProvider.REFRESH_TOKEN, null, 0);
		response.addCookie(access);
		response.addCookie(refresh);
	}

	private void createAuthentication(Long userId) {
		UserDetails user = userDetailsService.loadUserById(userId);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}
