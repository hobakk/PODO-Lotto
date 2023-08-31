package com.example.sixnumber.user.dto;

import javax.servlet.http.Cookie;

import com.example.sixnumber.global.util.JwtProvider;

import lombok.Getter;

@Getter
public class CookiesResponse {
	private final Cookie accessCookie;
	private final Cookie refreshCookie;

	public CookiesResponse(Cookie accessCookie, Cookie refreshCookie) {
		this.accessCookie = accessCookie;
		this.refreshCookie = refreshCookie;
	}

	public CookiesResponse() {
		this.accessCookie = new Cookie(JwtProvider.ACCESS_TOKEN, null);
		this.refreshCookie = new Cookie(JwtProvider.REFRESH_TOKEN, null);
	}
}
