package com.example.sixnumber.lotto.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class LottoResponse{
	private final List<Integer> countList;
	private final String value;

	@JsonCreator
	public LottoResponse(
		@JsonProperty("countList") List<Integer> countList,
		@JsonProperty("value") String value
	) {
		this.countList = new ArrayList<>(countList);
		this.value = value;
	}
}
