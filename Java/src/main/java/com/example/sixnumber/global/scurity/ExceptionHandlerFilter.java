package com.example.sixnumber.global.scurity;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.sixnumber.global.dto.ExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		ExceptionDto exceptionDto;

		try {
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			exceptionDto = new ExceptionDto(401, "RE_ISSUANCE", "");
			setExceptionDto(response, exceptionDto);
		}
	}

	private void setExceptionDto(HttpServletResponse response, ExceptionDto exceptionDto) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
		response.setStatus(exceptionDto.getCode());
		objectMapper.writeValue(response.getWriter(), exceptionDto);
	}

	// private boolean validateRefreshInCookie(String refreshTokenInCookie, String refreshPointer,
	// 	HttpServletResponse response) {
	//
	// 	if (isDifferent(refreshTokenInCookie, refreshPointer)) return true;
	//
	// 	String newAccessToken = jwtProvider.accessToken(refreshPointer);
	// 	updateAccessTokenCookie(response, newAccessToken);
	// 	createAuthentication(jwtProvider.getTokenInUserId(refreshTokenInCookie));
	// 	return false;
	// }
	//
	// private boolean isDifferent(String refreshTokenInCookie, String refreshPointer) {
	// 	String inRedisValue = redisDao.getValue(refreshPointer);
	// 	if (inRedisValue == null) return true; // refreshToken 의 TTL 이 만료되어 자동 삭제됬을 경우 생각해봐야함
	//
	// 	return !refreshTokenInCookie.equals(inRedisValue);
	// }
	//
	// private void updateAccessTokenCookie(HttpServletResponse response, String newAccessToken) {
	// 	Cookie cookie = jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, newAccessToken, 300);
	// 	response.addCookie(cookie);
	// }
}
