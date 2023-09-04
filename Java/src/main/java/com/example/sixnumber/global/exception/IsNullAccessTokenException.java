package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class IsNullAccessTokenException extends RuntimeException {
	private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
	private final String msg = "AccessToken 이 존재하지 않습니다";
}
