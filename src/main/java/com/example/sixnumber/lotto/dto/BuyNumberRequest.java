package com.example.sixnumber.lotto.dto;

import lombok.Getter;

@Getter
public class BuyNumberRequest {
	private final int value;
	private int repetition;	// 반복횟수

	public BuyNumberRequest(int value) {
		this.value = value;
	}

	public BuyNumberRequest(int value, int repetition) {
		this.value = value;
		this.repetition = repetition;
	}
}
