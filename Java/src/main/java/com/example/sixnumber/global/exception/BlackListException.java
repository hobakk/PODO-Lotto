package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

public class BlackListException extends BaseException{

	public BlackListException(String message) {
		super(HttpStatus.FORBIDDEN ,message);
	}
}
