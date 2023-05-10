package com.example.sixnumber.user.dto;

import lombok.Getter;

@Getter
public class ReleasePaidRequest {
	private final String msg;

	public ReleasePaidRequest(String msg) {
		this.msg = msg;
		if (!msg.equals("월정액 해지")) {
			throw new IllegalArgumentException("잘못된 입력입니다");
		}
	}
}
