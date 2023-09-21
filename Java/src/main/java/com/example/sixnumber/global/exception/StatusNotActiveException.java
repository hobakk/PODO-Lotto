package com.example.sixnumber.global.exception;

import org.springframework.http.HttpStatus;

public class StatusNotActiveException extends BaseException {
	public StatusNotActiveException() { super(HttpStatus.FORBIDDEN, "정지되거나 탈퇴된 계정입니다"); }
}
