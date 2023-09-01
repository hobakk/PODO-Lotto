package com.example.sixnumber.global.scurity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import com.example.sixnumber.global.dto.ExceptionDto;
import com.example.sixnumber.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	private final ObjectMapper objectMapper;
	private final RequestMatcher requestMatcher;
	private static ExceptionDto exception;

	public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.requestMatcher = request -> request.getRequestURI().startsWith("/api/admin/");
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {

		if (requestMatcher.matches(request)) exception = new ExceptionDto(ErrorCode.ONLY_ADMIN_ACCESS_API);
 		else exception = new ExceptionDto(ErrorCode.ACCESS_DENIED);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
		response.setStatus(HttpStatus.FORBIDDEN.value());
		objectMapper.writeValue(response.getWriter(), exception);
	}
}
