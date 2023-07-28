package com.example.sixnumber.lotto.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class TransformResponse {
	private final String data;
	private final int time;
	private final Long prize;
	private final int winner;
	private final List<Integer> topNumberList;
	private final int bonus;

	@JsonCreator
	public static TransformResponse create(
		@JsonProperty("data") String data,
		@JsonProperty("time") int time,
		@JsonProperty("prize") Long prize,
		@JsonProperty("winner") int winner,
		@JsonProperty("topNumberList") List<Integer> topNumberList,
		@JsonProperty("bonus") int bonus
	) {
		return new TransformResponse(data, time, prize, winner, topNumberList, bonus);
	}

	public TransformResponse(String data, int time, Long prize, int winner, List<Integer> topNumberList, int bonus) {
		this.data = data;
		this.time = time;
		this.prize = prize;
		this.winner = winner;
		this.topNumberList = new ArrayList<>(topNumberList);
		this.bonus = bonus;
	}
}