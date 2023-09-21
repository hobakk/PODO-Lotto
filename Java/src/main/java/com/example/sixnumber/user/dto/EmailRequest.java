package com.example.sixnumber.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailRequest {
	@NotBlank(message = "이메일 주소를 입력하세요.")
	@Email(message = "올바른 이메일 주소 형식이어야 합니다")
	private String email;
}
