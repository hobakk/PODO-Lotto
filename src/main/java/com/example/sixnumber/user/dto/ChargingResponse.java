package com.example.sixnumber.user.dto;

import lombok.Getter;

@Getter
public class ChargingResponse {
	private final String msg;
	private final int value;


	public ChargingResponse(String str) {
		this.msg = str.split("-")[1];
		this.value = Integer.parseInt(str.split("-")[2]);
	}
}
