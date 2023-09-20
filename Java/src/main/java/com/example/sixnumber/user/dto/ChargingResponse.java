package com.example.sixnumber.user.dto;

import lombok.Getter;

@Getter
public class ChargingResponse {
	private final String msg;
	private final int cash;
	private final String date;

	// value = msg-cash-date
	public ChargingResponse(String value) {
		String date = value.split("-")[2];

		this.msg = value.split("-")[0];
		this.cash = Integer.parseInt(value.split("-")[1]);
		this.date = date.substring(0, date.lastIndexOf(" "));
	}
}
