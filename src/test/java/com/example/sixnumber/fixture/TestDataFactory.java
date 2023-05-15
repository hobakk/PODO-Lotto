package com.example.sixnumber.fixture;

import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ReleasePaidRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.entity.Cash;
import com.example.sixnumber.user.entity.User;

public class TestDataFactory {

	public static User user() {
		User user = new User(signupRequest(), "ePassword");
		user.setCash("+", 5000);
		user.setRole("USER");
		user.setStatus("ACTIVE");
		return user;
	}

	public static User Admin() {
		User user = new User(signupRequest(), "ePassword");
		user.setAdmin();
		return user;
	}

	public static Cash cash() {
		return new Cash(user().getId(), chargingRequest());
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

	public static ChargingRequest chargingRequest() {
		return new ChargingRequest(
			"Msg",
			5000
		);
	}

	public static CashRequest cashRequest() {
		return new CashRequest(
			7L,
			7L,
			5000
		);
	}

}
