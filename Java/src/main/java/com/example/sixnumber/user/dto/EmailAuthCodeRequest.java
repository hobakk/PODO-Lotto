package com.example.sixnumber.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailAuthCodeRequest {
	private String email;
	private String authCode;
}
