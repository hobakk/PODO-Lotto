package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

public class InvalidInputException extends BaseException {
	private static final String MSG = "잘못된 입력값입니다";

	public InvalidInputException() {
		super(HttpStatus.BAD_REQUEST, MSG);
	}
}
