package com.example.sixnumber.global.dto;

import java.util.HashMap;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class MapApiResponse<K, T> extends ApiResponse {
	protected final HashMap<K, T> data;

	public MapApiResponse(int code, String msg, HashMap<K, T> data) {
		super(code, msg);
		this.data = data;
	}

	public static <K, T> MapApiResponse<K, T> ok(String msg, HashMap<K, T> data) {
		return new MapApiResponse<>(HttpStatus.OK.value(), msg, data);
	}
}
