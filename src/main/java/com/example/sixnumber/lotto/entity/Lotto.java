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
	@Column(name = "email", nullable = false)
	private String email;

	@ElementCollection
	@OrderColumn(name = "countList_index", nullable = false)
	private List<Integer> countList = new ArrayList<>(46);
	@Column(name = "creationDate", nullable = false)
	private String creationDate;

	public Lotto(String email, String creationDate, List<Integer> countList) {
		this.creationDate = creationDate;
		this.email = email;
		this.countList = countList;
	}
}
