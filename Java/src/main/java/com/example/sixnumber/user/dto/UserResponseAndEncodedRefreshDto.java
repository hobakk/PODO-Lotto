package com.example.sixnumber.user.dto;

import lombok.Getter;

@Getter
public class UserResponseAndEncodedRefreshDto {
	private final UserResponse userResponse;
	private final String encodedRefreshToken;

	public UserResponseAndEncodedRefreshDto(UserResponse userResponse, String encodedRefreshToken) {
		this.userResponse = userResponse;
		this.encodedRefreshToken = encodedRefreshToken;
	}
}
