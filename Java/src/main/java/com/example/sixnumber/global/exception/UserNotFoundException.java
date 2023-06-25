package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
	private static final String msg = "유저가 존재하지 않습니다";

	public UserNotFoundException() {
		super(HttpStatus.BAD_REQUEST, msg);
	}
}
