package com.example.sixnumber.user.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.example.sixnumber.user.dto.ChargingRequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Cash {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private User user;
	private String msg;
	private int value;

	public Cash(User user, ChargingRequest request) {
		this.user = user;
		this.msg = request.getMsg();
		this.value = request.getValue();
	}
}
