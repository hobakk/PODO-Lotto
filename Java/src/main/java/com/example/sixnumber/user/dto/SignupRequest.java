package com.example.sixnumber.user.dto;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {
	@Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "올바른 이메일 주소 형식이어야 합니다.")
	private String email;

	@Pattern(regexp = "^[A-Za-z0-9~!@#$%^&*=,.?]{8,60}$", message = "대소문자, 숫자, 특수문자 범주 안에 최소 8자 최대 60자")
	private String password;

	@Pattern(regexp = "^[A-Za-z0-9가-힣+_]{2,10}$", message = "대소문자, 숫자, 한글 범주 안에 최소 2자 최대 10자")
	private String nickname;
}
