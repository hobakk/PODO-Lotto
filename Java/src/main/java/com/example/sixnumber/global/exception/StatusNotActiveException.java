package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

public class StatusNotActiveException extends BaseException {
	public StatusNotActiveException(String message) {
		super(HttpStatus.FORBIDDEN, message);
	}
}
