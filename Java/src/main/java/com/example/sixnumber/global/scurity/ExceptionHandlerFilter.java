package com.example.sixnumber.global.scurity;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.sixnumber.global.exception.OnlyHaveRefreshTokenException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.sixnumber.global.dto.ExceptionDto;
import com.example.sixnumber.global.exception.AccessTokenIsExpiredException;
import com.example.sixnumber.global.exception.RefreshTokenIsNullException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		} catch (AccessTokenIsExpiredException e) {
			exceptionDto = new ExceptionDto(401, "RE_ISSUANCE", "");
			setExceptionDto(response, exceptionDto);
		} catch (RefreshTokenIsNullException e) {
			exceptionDto = new ExceptionDto(400, "REFRESH_ISNULL", "Redis 에 refreshToken 이 없습니다 ");
			setExceptionDto(response, exceptionDto);
		} catch (OnlyHaveRefreshTokenException e) {
			exceptionDto = new ExceptionDto(401, "ONLY_HAVE_REFRESH", "");
			setExceptionDto(response, exceptionDto);
		}
	}

	private void setExceptionDto(HttpServletResponse response, ExceptionDto exceptionDto) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
		response.setStatus(exceptionDto.getCode());
		objectMapper.writeValue(response.getWriter(), exceptionDto);
	}
}
