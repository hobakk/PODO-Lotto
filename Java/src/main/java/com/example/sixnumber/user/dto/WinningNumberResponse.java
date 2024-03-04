package com.example.sixnumber.user.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class WinningNumberResponse {
	private final String numberSentence;
	private static final Map<Integer, Integer> rankMap;
	private final int rank;

	static {
		rankMap = new HashMap<>();
		rankMap.put(3, 5);
		rankMap.put(4, 4);
		rankMap.put(5, 3);
		rankMap.put(6, 1);
		rankMap.put(7, 2);
	}

	public WinningNumberResponse(String numberSentence, int numberOfWins) {
		this.numberSentence = numberSentence.trim();
		this.rank = rankMap.getOrDefault(numberOfWins, -1);
	}
}
