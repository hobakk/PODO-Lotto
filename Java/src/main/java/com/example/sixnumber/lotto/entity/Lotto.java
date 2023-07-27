package com.example.sixnumber.lotto.entity;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	@Column(name = "subject", nullable = false)
	private String subject;
	@ElementCollection
	@OrderColumn(name = "countList_index", nullable = false)
	private List<Integer> countList = new ArrayList<>(46);
	@Column(name = "creationDate")
	private YearMonth creationDate;
	@Column(name = "value")
	private String topNumber;

	public Lotto(String subject, String email, YearMonth creationDate, List<Integer> countList, String topNumber) {
		this.subject = subject;
		this.creationDate = creationDate;
		this.email = email;
		this.countList = countList;
		this.topNumber = topNumber;
	}
}
