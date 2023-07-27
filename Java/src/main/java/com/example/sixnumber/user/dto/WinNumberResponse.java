package com.example.sixnumber.user.dto;

import java.util.List;

import com.example.sixnumber.lotto.entity.WinNumber;

import lombok.Getter;

@Getter
public class WinNumberResponse {
	private final List<WinNumber> winNumberList;

	public WinNumberResponse(List<WinNumber> winNumberList) {
		this.winNumberList = winNumberList;
	}
}
