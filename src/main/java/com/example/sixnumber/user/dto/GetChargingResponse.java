package com.example.sixnumber.user.dto;

import com.example.sixnumber.user.entity.Cash;

import lombok.Getter;

@Getter
public class GetChargingResponse {
	private final String msg;
	private final int value;
	private String str;

	public GetChargingResponse(Cash cash) {
		this.msg = cash.getMsg();
		this.value = cash.getValue();
		switch (cash.getProcessing()) {
			case BEFORE -> str = "충전 대기중";
			case AFTER -> str = "충전 처리 완료";
		}
	}
}
