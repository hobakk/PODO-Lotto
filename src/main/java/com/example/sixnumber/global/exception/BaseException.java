package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
	private final HttpStatus status;

	public BaseException(HttpStatus status, String msg) {
		super(msg);
		this.status = status;
	}

	public BaseException(HttpStatus status) {
		this.status = status;
	}
}
