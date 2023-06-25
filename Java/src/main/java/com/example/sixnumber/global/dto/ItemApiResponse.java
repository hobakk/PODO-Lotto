package com.example.sixnumber.global.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ItemApiResponse<T> extends ApiResponse {
	protected final T data;

	public ItemApiResponse(int code, String msg, T data) {
		super(code, msg);
		this.data = data;
	}

	public static <T> ItemApiResponse<T> ok(String msg, T data) {
		return new ItemApiResponse<>(HttpStatus.OK.value(), msg, data);
	}
}
