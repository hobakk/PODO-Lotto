package com.example.sixnumber.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WinNumberRequest {
	private String date;
	private int time;
	private Long prize;
	private int winner;
	private String numbers;
}
