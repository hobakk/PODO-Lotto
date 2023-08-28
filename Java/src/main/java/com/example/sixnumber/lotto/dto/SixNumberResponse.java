package com.example.sixnumber.lotto.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.sixnumber.lotto.entity.SixNumber;

import lombok.Getter;

@Getter
public class SixNumberResponse {
	private final String date;
	private final List<String> numberList;

	public SixNumberResponse(SixNumber sixNumber) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");

		this.date = sixNumber.getBuyDate().format(formatter);
		this.numberList = sixNumber.getNumberList();
	}
}
