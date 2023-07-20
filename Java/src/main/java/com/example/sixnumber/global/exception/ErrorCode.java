package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	BREAK_THE_ROLE(HttpStatus.FORBIDDEN, "규정 위반으로 홈페이지를 이용할 수 없습니다"),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다"),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"),;

	private final HttpStatus httpStatus;
	private final String msg;
}
