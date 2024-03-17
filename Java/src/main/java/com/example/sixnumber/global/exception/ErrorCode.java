package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"),
	NOT_FOUND(HttpStatus.NOT_FOUND, "해당 정보가 존재하지 않습니다"),

	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Expired Tokens, 만료된 토큰 입니다"),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 Token 입니다"),
	DONT_LOGIN(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요"),

	INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다"),
	MISSING_DATA(HttpStatus.BAD_REQUEST, "UnifiedResponse data 가 누락되었습니다."),
	NO_MATCHING_INFO_FOUND(HttpStatus.BAD_REQUEST, "일치하는 정보를 찾을 수 없습니다"),
	NOT_OAUTH2_LOGIN(HttpStatus.BAD_REQUEST, "해당 이메일은 간편 로그인을 이용해 주세요"),
	OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "범위를 벗어났습니다"),

	BREAK_THE_ROLE(HttpStatus.FORBIDDEN, "규정 위반으로 홈페이지를 이용할 수 없습니다"),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 리소스에 대한 권한이 없습니다"),
	ONLY_ADMIN_ACCESS_API(HttpStatus.FORBIDDEN, "관리자만 접근 가는한 API 입니다"),

	;

	private final HttpStatus httpStatus;
	private final String message;
}
