package com.example.sixnumber.lotto.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class WinNumbersResponse {
	private final List<TransformResponse> winNumberList;

	@JsonCreator
	public WinNumbersResponse(@JsonProperty("winNumberList") List<TransformResponse> transformResponses) {
		this.winNumberList = transformResponses;
	}
}
