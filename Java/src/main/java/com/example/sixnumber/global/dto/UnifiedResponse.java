package com.example.sixnumber.global.dto;

import org.springframework.http.HttpStatus;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class UnifiedResponse<T> {
	private final int code;
	private final String msg;
	private T data;

	private static final int OK = HttpStatus.OK.value();
	private static final int CREATED = HttpStatus.CREATED.value();
	private static final int BAD_REQUEST = HttpStatus.BAD_REQUEST.value();

	public UnifiedResponse(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public UnifiedResponse(int code, String msg, T data) {
		if (data == null) throw new CustomException(ErrorCode.MISSING_DATA);

		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public static <T> UnifiedResponse<T> ok(String msg) {
		return new UnifiedResponse<>(OK, msg);
	}

	public static <T> UnifiedResponse<T> ok(String msg, T data) {
		return new UnifiedResponse<>(OK, msg, data);
	}

	public static <T> UnifiedResponse<T> create(String msg) {
		return new UnifiedResponse<>(CREATED, msg);
	}

	public static <T> UnifiedResponse<T> create(String msg, T data) {
		return new UnifiedResponse<>(CREATED, msg, data);
	}

	public static <T> UnifiedResponse<T> badRequest(String msg) { return new UnifiedResponse<>(BAD_REQUEST, msg); }
}
