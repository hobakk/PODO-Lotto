package com.example.sixnumber.user.dto;

import lombok.Getter;

@Getter
public class ChargingResponse {
	private final String msg;
	private final int cash;

	// str = id-msg-cash
	public ChargingResponse(String str) {
		this.msg = str.split("-")[1];
		this.cash = Integer.parseInt(str.split("-")[2]);
	}
}
