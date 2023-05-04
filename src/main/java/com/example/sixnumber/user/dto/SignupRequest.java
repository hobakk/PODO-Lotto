package com.example.sixnumber.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignupRequest {
	private String email;
	private String password;
	private String nickname;
}
