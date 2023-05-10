package com.example.sixnumber.global.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ListApiResponse<T> extends ApiResponse {
	protected final List<T> data;

	public ListApiResponse(int code, String msg, List<T> data) {
		super(code, msg);
		this.data = data;
	}

	public static <T> ListApiResponse<T> ok(String msg, List<T> data) {
		return new ListApiResponse<>(HttpStatus.OK.value(), msg, data);
	}
}
