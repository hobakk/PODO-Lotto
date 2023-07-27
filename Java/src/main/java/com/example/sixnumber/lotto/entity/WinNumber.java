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

import com.example.sixnumber.user.dto.WinNumberRequest;

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
	private List<Integer> topNumbers;

	public WinNumber(WinNumberRequest request) {
		this.data = request.getDate();
		this.time = request.getTime();
		this.prize = request.getPrize();
		this.winner = request.getWinner();
		this.topNumbers = Arrays.stream(request.getNumbers()
							.split(" "))
							.map(Integer::parseInt)
							.collect(Collectors.toList());
	}
}
