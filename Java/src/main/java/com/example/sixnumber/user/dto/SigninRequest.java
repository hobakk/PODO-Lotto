package com.example.sixnumber.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SigninRequest {
	@NotBlank(message = "이메일을 입력해주세요")
	@Email
	private String email;
	private String password;
}
