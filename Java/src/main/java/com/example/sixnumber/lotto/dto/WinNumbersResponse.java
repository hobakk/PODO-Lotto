package com.example.sixnumber.lotto.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.sixnumber.lotto.entity.WinNumber;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class WinNumbersResponse {
	private final List<FullFieldOfWinNumber> winNumberList;

	@JsonCreator
	public WinNumbersResponse(@JsonProperty("winNumberList") List<WinNumber> winNumberList) {
		this.winNumberList = winNumberList.stream()
				.map(FullFieldOfWinNumber::new)
				.collect(Collectors.toList());
	}
}
