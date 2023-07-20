package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

import com.example.sixnumber.global.exception.BaseException;

public class OverlapException extends BaseException {
	public OverlapException(String msg) {
		super(HttpStatus.BAD_REQUEST, msg);
	}
}

