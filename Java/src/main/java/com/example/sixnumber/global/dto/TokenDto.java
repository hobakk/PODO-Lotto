package com.example.sixnumber.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class TokenDto {
	private String accessToken;
	private String refreshToken;
	private String refreshPointer;

	public TokenDto(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.refreshPointer = null;
	}

	public TokenDto(String accessToken, String refreshToken, String refreshPointer) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.refreshPointer = refreshPointer;
	}
}
