package com.example.sixnumber.lotto.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderColumn;

import com.example.sixnumber.global.util.TimeStamped;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SixNumber extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	private Long userId;
	private LocalDate buyDate;

	@ElementCollection
	@OrderColumn(name = "number_index")
	private List<String> numberList = new ArrayList<>(6);

	public SixNumber(Long userId, LocalDate buyDate, List<String> numberList) {
		this.userId = userId;
		this.buyDate = buyDate;
		this.numberList = numberList;
	}
}
