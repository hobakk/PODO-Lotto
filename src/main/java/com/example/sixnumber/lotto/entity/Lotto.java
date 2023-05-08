package com.example.sixnumber.lotto.entity;

import java.time.YearMonth;
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
public class Lotto extends TimeStamped {

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
	@Column(name = "creationDate", nullable = false)
	private YearMonth creationDate;

	public Lotto(String subject, String email, YearMonth creationDate, List<Integer> countList) {
		this.subject = subject;
		this.creationDate = creationDate;
		this.email = email;
		this.countList = countList;
	}
}
