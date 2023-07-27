package com.example.sixnumber.lotto.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LottoResponse implements Serializable {
	private List<Integer> countList;
	private String value;

	public LottoResponse(List<Integer> countList, String value) {
		this.countList = countList;
		this.value = value;
	}

	@JsonCreator
	public static LottoResponse create(@JsonProperty("countList") List<Integer> countList,
		@JsonProperty("value") String value) {
		return new LottoResponse(countList, value);
	}
}
