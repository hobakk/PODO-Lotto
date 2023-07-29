package com.example.sixnumber.lotto.dto;

import java.time.YearMonth;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class YearMonthResponse {
	private final List<String> yearMonthList;

	@JsonCreator
	public YearMonthResponse(@JsonProperty("yearMonthList") List<String> yearMonthList) {
		this.yearMonthList = yearMonthList;
	}
}
