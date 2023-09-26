package com.example.sixnumber.user.dto;

import com.example.sixnumber.user.entity.Statement;

import lombok.Getter;

@Getter
public class StatementResponse {
	private final String subject;
	private final String localDate;
	private final int cash;
	private final String msg;
	private final boolean modify;

	public StatementResponse(Statement statement) {
		this.subject = statement.getSubject();
		this.localDate = statement.getLocalDate().toString();
		this.cash = statement.getCash();
		this.msg = statement.getMsg();
		this.modify = statement.isModify();
	}
}
