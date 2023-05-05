package com.example.sixnumber.lotto.entity;

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
public class Lotto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ElementCollection
	@OrderColumn(name = "countList_index")
	private List<Integer> countList = new ArrayList<>(46);
	private String creationDate;

	public Lotto(List<Integer> countList, String creationDate) {
		this.countList = countList;
		this.creationDate = creationDate;
	}
}
