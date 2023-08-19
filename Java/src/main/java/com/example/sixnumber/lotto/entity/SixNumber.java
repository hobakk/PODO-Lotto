package com.example.sixnumber.lotto.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderColumn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SixNumber {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "userId")
	private Long userId;
	@Column(name = "buyDate", nullable = false)
	private LocalDateTime buyDate;

	@ElementCollection
	@OrderColumn(name = "number_index")
	private List<String> numberList = new ArrayList<>(6);

	public SixNumber(Long userId, LocalDateTime buyDate, List<String> numberList) {
		this.userId = userId;
		this.buyDate = buyDate;
		this.numberList = numberList;
	}
}
