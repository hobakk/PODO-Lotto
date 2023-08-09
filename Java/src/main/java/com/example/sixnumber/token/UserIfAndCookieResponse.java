package com.example.sixnumber.token;

import javax.servlet.http.Cookie;
import com.example.sixnumber.user.dto.MyInformationResponse;

import lombok.Getter;

@Getter
public class UserIfAndCookieResponse {
	private final MyInformationResponse response;
	private final Cookie cookie;

	public UserIfAndCookieResponse(MyInformationResponse response, Cookie cookie) {
		this.response = response;
		this.cookie = cookie;
	}
}
