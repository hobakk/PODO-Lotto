package com.example.sixnumber.lotto.dto;

import lombok.Getter;

@Getter
public class BuyNumberRequest {
	private final int value;

	public BuyNumberRequest(int value) {
		this.value = value;
	}
}
