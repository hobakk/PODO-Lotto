package com.example.sixnumber.user.dto;

import javax.servlet.http.Cookie;

import lombok.Getter;

@Getter
public class CookiesResponse {
	private final Cookie accessCookie;
	private final Cookie refreshCookie;

	public CookiesResponse(Cookie accessCookie, Cookie refreshCookie) {
		this.accessCookie = accessCookie;
		this.refreshCookie = refreshCookie;
	}
}
