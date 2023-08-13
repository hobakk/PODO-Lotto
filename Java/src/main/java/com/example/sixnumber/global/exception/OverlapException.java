package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

public class OverlapException extends BaseException {
	public OverlapException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}
}

