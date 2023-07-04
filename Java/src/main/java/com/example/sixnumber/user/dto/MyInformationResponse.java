package com.example.sixnumber.user.dto;

import java.util.List;

import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.UserRole;

import lombok.Getter;

@Getter
public class MyInformationResponse {
	private final String email;
	private final String nickname;
	private final int cash;
	private final UserRole role;
	private final List<String> statement;

	public MyInformationResponse(User user) {
		this.email = user.getEmail();
		this.nickname = user.getNickname();
		this.cash = user.getCash();
		this.role = user.getRole();
		this.statement = user.getStatement();
	}
}
