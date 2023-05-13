package com.example.sixnumber.fixture;

import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.entity.User;

public class TestDataFactory {

	public static User user() {
		User user = new User(signupRequest(), "ePassword");
		user.setCash("+", 5000);
		return user;
	}

	public static SignupRequest signupRequest() {
		return new SignupRequest(
			"test@email.com",
			"password1!",
			"nickname"
		);
	}

	public static SigninRequest signinRequest() {
		return new SigninRequest(
			"test@email.com",
			"password1!"
		);
	}


}
