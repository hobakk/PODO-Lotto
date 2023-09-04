package com.example.sixnumber.user.dto;

import javax.servlet.http.Cookie;

import lombok.Getter;

@Getter
public class CookieAndTokenResponse {
	private final Cookie accessCookie;
	private final String enCodedRefreshToken;

	public CookieAndTokenResponse(Cookie accessCookie, String enCodedRefreshToken) {
		this.accessCookie = accessCookie;
		this.enCodedRefreshToken = enCodedRefreshToken;
	}
}
