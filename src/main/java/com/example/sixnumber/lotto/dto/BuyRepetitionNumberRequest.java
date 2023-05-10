package com.example.sixnumber.lotto.dto;

import lombok.Getter;

@Getter
public class BuyRepetitionNumberRequest {
	private final int value;
	private final int repetition;

	public BuyRepetitionNumberRequest(int value, int repetition) {
		this.value = value;
		this.repetition = repetition;
		if (repetition < 1000) {
			throw new IllegalArgumentException("1000 보다 작은 반복횟수는 이용할 수 없습니다");
		}
	}
}
