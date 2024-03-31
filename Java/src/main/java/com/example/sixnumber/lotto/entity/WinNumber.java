package com.example.sixnumber.lotto.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderColumn;

import com.example.sixnumber.global.dto.NumberListAndBonusResponse;
import com.example.sixnumber.lotto.dto.WinNumberRequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class WinNumber {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String date;
	@Column(nullable = false)
	private int time;
	@Column(nullable = false)
	private Long prize;
	@Column(nullable = false)
	private int winner;
	@ElementCollection
	@OrderColumn(name = "topNumbers_index", nullable = false)
	private List<Integer> topNumberList;
	@Column(nullable = false)
	private int bonus;

	public WinNumber(WinNumberRequest request) {
		NumberListAndBonusResponse response = request.getNumberListAndBonus();

		this.date = request.getDate();
		this.time = request.getTime();
		this.prize = request.getPrize();
		this.winner = request.getWinner();
		this.topNumberList = response.getNumberList();
		this.bonus = response.getBonus();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof WinNumber))
			return false;

		WinNumber winNumber = (WinNumber)o;
		return id.equals(winNumber.id)
			&& topNumberList.equals(winNumber.topNumberList)
			&& bonus == winNumber.bonus;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + topNumberList.hashCode();
		result = 31 * result + bonus;
		return result;
	}
}
