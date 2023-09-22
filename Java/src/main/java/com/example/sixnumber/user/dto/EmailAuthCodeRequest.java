package com.example.sixnumber.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailAuthCodeRequest {
	private String email;
	private String authCode;
}
