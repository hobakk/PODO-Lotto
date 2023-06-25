package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

public class BreakTheRulesException extends BaseException{
	private static final String MSG = "규정 위반으로 홈페이지를 이용할 수 없습니다";

	public BreakTheRulesException() {
		super(HttpStatus.FORBIDDEN, MSG);
	}
}
