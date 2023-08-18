package com.example.sixnumber.user.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class AdminGetChargingResponse {
	private Long userId;
	private String msg;
	private int cash;

	public AdminGetChargingResponse(String str) {
		String[] idMsgValue = str.split("-");
		this.userId = Long.parseLong(idMsgValue[0]);
		this.msg = idMsgValue[1];
		this.cash = Integer.parseInt(idMsgValue[2]);
	}

	public AdminGetChargingResponse(List<String> list) {
		for (String redisValue : list) {
			String[] idMsgValue = redisValue.split("-");
			this.userId = Long.parseLong(idMsgValue[0]);
			this.msg = idMsgValue[1];
			this.cash = Integer.parseInt(idMsgValue[2]);
		}
	}
}
