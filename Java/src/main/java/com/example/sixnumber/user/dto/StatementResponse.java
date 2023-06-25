package com.example.sixnumber.user.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class StatementResponse {
	private final String localDate;
	private final String msg;

	public StatementResponse(String[] strings) {
		this.localDate = strings[0];
		this.msg = strings[1];
	}
}
