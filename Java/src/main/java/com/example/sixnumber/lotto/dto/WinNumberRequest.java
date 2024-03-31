package com.example.sixnumber.lotto.dto;

import com.example.sixnumber.global.dto.NumberListAndBonusResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WinNumberRequest {
	private String date;
	private int time;
	private Long prize;
	private int winner;
	private String numbers;

	public NumberListAndBonusResponse getNumberListAndBonus() {
		return new NumberListAndBonusResponse(this.numbers);
	}
}
