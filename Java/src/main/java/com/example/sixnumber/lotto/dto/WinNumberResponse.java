package com.example.sixnumber.lotto.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class WinNumberResponse {
	private final List<TransformResponse> winNumberList;

	@JsonCreator
	public WinNumberResponse(@JsonProperty("winNumberList") List<TransformResponse> transformResponses) {
		this.winNumberList = transformResponses;
	}
}
