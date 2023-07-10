package com.example.sixnumber.user.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class WinNumberResponse {
	private final String date;
	private final int time;
	private final Long prize;
	private final int winner;
	private final int bonus;
	private final List<Integer> numList;

	public WinNumberResponse(String str) {
		String[] value = str.split(",");
		this.date = value[1];
		this.time = Integer.parseInt(value[0]);
		this.prize = Long.parseLong(value[2]);
		this.winner = Integer.parseInt(value[3]);

		String[] numListStr = value[4].split(" ");
		List<Integer> winNumbers = new ArrayList<>();
		for (int i =0; i < numListStr.length-1; i++) {
			winNumbers.add(Integer.parseInt(numListStr[i]));
		}
		this.bonus = Integer.parseInt(numListStr[numListStr.length-1]);
		this.numList = winNumbers;
	}
}
