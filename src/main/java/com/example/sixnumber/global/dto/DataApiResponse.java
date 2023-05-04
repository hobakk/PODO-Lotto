package com.example.sixnumber.global.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class DataApiResponse<T> extends ApiResponse {
	protected final List<T> data;

	public DataApiResponse(int code, String mes, List<T> data) {
		super(code, mes);
		this.data = data;
	}

	public static <T> DataApiResponse<T> ok(String msg, List<T> data) {
		return new DataApiResponse<>(HttpStatus.OK.value(), msg, data);
	}
}
