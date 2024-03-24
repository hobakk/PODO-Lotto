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

	public TokenDto(String accessToken) {
		this.accessToken = accessToken;
	}

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

	public Boolean hasBothToken(){
		return this.accessToken != null && this.refreshToken != null;
	}
	public Boolean hasAccessToken() { return this.accessToken != null; }
	public Boolean hasRefreshToken() { return this.refreshToken != null; }
	public Boolean onlyHaveRefreshToken() { return this.accessToken == null && this.refreshToken != null; }
	public Boolean onlyHaveAccessToken() { return this.accessToken != null && this.refreshToken == null; }
	public Boolean isNotEmpty() { return hasAccessToken() || hasRefreshToken(); }
}
