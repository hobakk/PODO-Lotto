package com.example.sixnumber.lotto.entity;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;

import com.example.sixnumber.global.util.TimeStamped;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Lotto extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ElementCollection
	@CollectionTable(name = "lotto_number")
	@MapKeyColumn(name = "number")
	@Column(name = "count")
	private HashMap<Integer, Integer> numberList = new HashMap<>();
	private LocalDateTime creationDate;

	public Lotto(HashMap<Integer, Integer> numberList, LocalDateTime creationDate) {
		this.numberList = numberList;
		this.creationDate = creationDate;
	}
}
