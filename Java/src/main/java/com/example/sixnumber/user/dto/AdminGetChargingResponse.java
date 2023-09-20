package com.example.sixnumber.user.dto;

import lombok.Getter;

@Getter
public class AdminGetChargingResponse {
	private final Long userId;
	private final String msg;
	private final int cash;
	private final String date;

	// value = id-msg-cash-date
	public AdminGetChargingResponse(String sentence) {
		String[] value = sentence.split("-");
		this.userId = Long.parseLong(value[0]);
		this.msg = value[1];
		this.cash = Integer.parseInt(value[2]);
		this.date = value[3].substring(0, value[3].lastIndexOf(" "));
	}
}
