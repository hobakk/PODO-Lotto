package com.example.sixnumber.global.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiResponse {
	protected final int code;
	protected final String mes;

	public ApiResponse(int code, String mes) {
		this.code = code;
		this.mes = mes;
	}

	public static ApiResponse ok(String msg) { return new ApiResponse(HttpStatus.OK.value(), msg); }
	public static ApiResponse create(String msg) { return new ApiResponse(HttpStatus.CREATED.value(), msg); }
}