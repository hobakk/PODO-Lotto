package com.example.sixnumber.user.dto;

import lombok.Getter;

@Getter
public class StatementResponse {
	private final String localDate;
	private final String msg;

	public StatementResponse(String sentence) {
		String[] value = sentence.split(",");
		this.localDate = value[0];
		this.msg = value[1];
	}
}
