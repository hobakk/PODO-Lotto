package com.example.sixnumber.lotto.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class SixNumberResponse {
	private final String date;
	private final List<String> numberList;

	public SixNumberResponse(String date, List<String> numberList) {
		this.date = date;
		this.numberList = numberList;
	}
}
