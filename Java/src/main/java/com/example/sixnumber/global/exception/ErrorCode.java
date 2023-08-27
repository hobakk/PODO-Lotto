package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	BREAK_THE_ROLE(HttpStatus.FORBIDDEN, "규정 위반으로 홈페이지를 이용할 수 없습니다"),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다"),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Expired Tokens, 만료된 토큰 입니다"),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 Token 입니다"),
	MISSING_DATA(HttpStatus.BAD_REQUEST, "UnifiedResponse data 가 누락되었습니다."),
	NO_MATCHING_INFO_FOUND(HttpStatus.BAD_REQUEST, "일치하는 정보를 찾을 수 없습니다");

	private final HttpStatus httpStatus;
	private final String message;
}
