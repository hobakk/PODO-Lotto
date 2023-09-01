package com.example.sixnumber.global.dto;

import com.example.sixnumber.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ExceptionDto {
	private final int code;
	private final String exceptionType;
	private String msg;

	public ExceptionDto(ErrorCode code) {
		this.code = code.getHttpStatus().value();
		this.exceptionType = code.name();
		this.msg = code.getMessage();
	}

	public void setExceptionDtoChangedMsg(String newMsg) {
		this.msg = newMsg;
	}
}
