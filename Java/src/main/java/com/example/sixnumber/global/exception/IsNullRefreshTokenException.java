package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class IsNullRefreshTokenException extends RuntimeException {
	private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
	private final String msg = "RefreshToken 이 존재하지 않습니다";
}
