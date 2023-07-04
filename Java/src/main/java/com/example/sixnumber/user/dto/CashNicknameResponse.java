package com.example.sixnumber.user.dto;

import com.example.sixnumber.user.entity.User;

import lombok.Getter;

@Getter
public class CashNicknameResponse {
	private final int cash;
	private final String nickname;

	public CashNicknameResponse(User user) {
		this.cash = user.getCash();
		this.nickname = user.getNickname();
	}
}
