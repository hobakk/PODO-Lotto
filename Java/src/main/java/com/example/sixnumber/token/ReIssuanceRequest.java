package com.example.sixnumber.token;

import lombok.Getter;

@Getter
public class ReIssuanceRequest {
	private Long userId;
	private String email;
	private String refreshTokenValue;
}
