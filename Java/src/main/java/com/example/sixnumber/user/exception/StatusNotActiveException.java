package com.example.sixnumber.user.exception;

import org.springframework.http.HttpStatus;

import com.example.sixnumber.global.exception.BaseException;

public class StatusNotActiveException extends BaseException {
	public StatusNotActiveException(String msg) {
		super(HttpStatus.FORBIDDEN, msg);
	}
}
