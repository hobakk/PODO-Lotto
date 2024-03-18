package com.example.sixnumber.lotto.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.sixnumber.lotto.entity.WinNumber;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class TransformResponse {
	private final String date;
	private final int time;
	private final Long prize;
	private final int winner;
	private final List<Integer> topNumberList;
	private final int bonus;

	@JsonCreator
	public static TransformResponse create(
		@JsonProperty("date") String data,
		@JsonProperty("time") int time,
		@JsonProperty("prize") Long prize,
		@JsonProperty("winner") int winner,
		@JsonProperty("topNumberList") List<Integer> topNumberList,
		@JsonProperty("bonus") int bonus
	) {
		return new TransformResponse(data, time, prize, winner, topNumberList, bonus);
	}

	public TransformResponse(String date, int time, Long prize, int winner, List<Integer> topNumberList, int bonus) {
		this.date = date;
		this.time = time;
		this.prize = prize;
		this.winner = winner;
		this.topNumberList = new ArrayList<>(topNumberList);
		this.bonus = bonus;
	}

	public TransformResponse(WinNumber winNumber) {
		this.date = winNumber.getDate();
		this.time = winNumber.getTime();
		this.prize = winNumber.getPrize();
		this.winner = winNumber.getWinner();
		this.topNumberList = new ArrayList<>(winNumber.getTopNumberList());
		this.bonus = winNumber.getBonus();
	}
}