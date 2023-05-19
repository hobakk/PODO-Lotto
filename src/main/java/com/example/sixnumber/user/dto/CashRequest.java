package com.example.sixnumber.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashRequest {
	private Long userId;
	private Long cashId;
	private int value;

}
