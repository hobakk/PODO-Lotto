package com.example.sixnumber.lotto.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.sixnumber.lotto.entity.SixNumber;

import lombok.Getter;

@Getter
public class SixNumberResponse {
	private final LocalDateTime localDateTime;
	private final List<String> numberList;

	public SixNumberResponse(SixNumber sixNumber) {
		this.localDateTime = sixNumber.getBuyDate();
		this.numberList = sixNumber.getNumberList();
	}
}
