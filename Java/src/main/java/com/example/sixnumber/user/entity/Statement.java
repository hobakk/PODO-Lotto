package com.example.sixnumber.user.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "statements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Statement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private String subject;
	private LocalDate localDate;
	private int cash;
	private String msg;
	private boolean modify;

	public Statement(User user, String subject, int cash, String msg) {
		this.user = user;
		this.subject = subject;
		this.localDate = LocalDate.now();
		this.cash = cash;
		this.msg = msg;
		this.modify = false;
	}

	public Statement(User user, String subject, int cash) {
		this.user = user;
		this.subject = subject;
		this.localDate = LocalDate.now();
		this.cash = cash;
		this.msg = null;
		this.modify = false;
	}

	private void modification() { this.modify = true; }

	private void modifyMsg(String msg) {
		this.msg = msg;
		modification();
	}
}
