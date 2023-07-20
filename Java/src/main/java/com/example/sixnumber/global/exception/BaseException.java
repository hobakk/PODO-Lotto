package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BaseException extends RuntimeException {
	private final HttpStatus status;

	public BaseException(HttpStatus status, String msg) {
		super(msg);
		this.status = status;
	}
}
