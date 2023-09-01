package com.example.sixnumber.global.scurity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.example.sixnumber.global.dto.ExceptionDto;
import com.example.sixnumber.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	private final ObjectMapper objectMapper;
	private static final ExceptionDto exception = new ExceptionDto(ErrorCode.ACCESS_DENIED);

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {

		response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
		response.setStatus(HttpStatus.FORBIDDEN.value());
		objectMapper.writeValue(response.getWriter(), exception);
	}
}
