package com.example.sixnumber.user.exception;

import org.springframework.http.HttpStatus;

import com.example.sixnumber.global.exception.BaseException;

public class OverlapException extends BaseException {
	private static String msg;

	public OverlapException(String msg) {
		super(HttpStatus.BAD_REQUEST, msg);
	}
}

