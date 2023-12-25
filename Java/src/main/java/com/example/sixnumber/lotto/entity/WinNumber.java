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
	private String data;
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
		List<Integer> bonusInclude = Arrays.stream(request.getNumbers()
				.split(" ")).map(Integer::parseInt).collect(Collectors.toList());
		List<Integer> topNumberList = bonusInclude.subList(0, bonusInclude.size()-1);

		this.data = request.getDate();
		this.time = request.getTime();
		this.prize = request.getPrize();
		this.winner = request.getWinner();
		this.topNumberList = topNumberList;
		this.bonus = bonusInclude.subList(bonusInclude.size()-1, bonusInclude.size()).get(0);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof WinNumber))
			return false;

		WinNumber winNumber = (WinNumber)o;

		if (bonus != winNumber.bonus)
			return false;
		if (!id.equals(winNumber.id))
			return false;
		return topNumberList.equals(winNumber.topNumberList);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + topNumberList.hashCode();
		result = 31 * result + bonus;
		return result;
	}
}
