package com.example.sixnumber.global.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class UnifiedResponse<T> {
	private final int code;
	private final String message;
	private T data;

	public UnifiedResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public UnifiedResponse(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> UnifiedResponse<T> ok(String message) {
		return new UnifiedResponse<>(HttpStatus.OK.value(), message);
	}

	public static <T> UnifiedResponse<T> ok(String message, T data) {
		return new UnifiedResponse<>(HttpStatus.OK.value(), message, data);
	}

	public static <T> UnifiedResponse<T> create(String message) {
		return new UnifiedResponse<>(HttpStatus.CREATED.value(), message);
	}

	public static <T> UnifiedResponse<T> create(String message, T data) {
		return new UnifiedResponse<>(HttpStatus.CREATED.value(), message, data);
	}
}
