package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AccessTokenIsExpiredException extends RuntimeException{
	private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
	private final String msg = "AccessToken 이 만료되었습니다";
}
