package com.example.sixnumber.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReIssuanceRequest {
	private Long userId;
	private String email;
	private String refreshToken;
}
