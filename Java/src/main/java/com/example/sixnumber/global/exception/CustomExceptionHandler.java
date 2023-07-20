package com.example.sixnumber.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<ErrorResponseEntity> comprehensive(CustomException e) {
		return ErrorResponseEntity.comprehensive(e.getErrorCode());
	}

	@ExceptionHandler({StatusNotActiveException.class, OverlapException.class})
	protected ResponseEntity<ErrorResponseEntity> individual(BaseException e) {
		return ErrorResponseEntity.individual(e);
	}
}
