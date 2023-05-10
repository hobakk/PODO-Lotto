package com.example.sixnumber.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.type.Processing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Cash {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	private Long userId;
	private String msg;
	private int value;
	@Enumerated(EnumType.STRING)
	private Processing processing;

	public Cash(Long userId, ChargingRequest request) {
		this.userId = userId;
		this.msg = request.getMsg();
		this.value = request.getValue();
		this.processing = Processing.BEFORE;
	}

	public void setProcessingAfter() {
		this.processing = Processing.AFTER;
	}
}
