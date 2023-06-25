package com.example.sixnumber.lotto.dto;

import com.example.sixnumber.lotto.entity.Lotto;

import lombok.Getter;

@Getter
public class LottoResponse {
	private final String statistics;
	private final String value;

	public LottoResponse(Lotto lotto) {
		this.statistics = lotto.getStatistics();
		this.value = lotto.getValue();
	}

	public LottoResponse(String statistics, String value) {
		this.statistics = statistics;
		this.value = value;
	}
}
