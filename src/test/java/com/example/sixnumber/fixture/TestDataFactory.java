package com.example.sixnumber.fixture;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.dto.StatisticalNumberRequest;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.entity.Cash;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

public class TestDataFactory {

	public static User user() {
		User user = new User(signupRequest(), "ePassword");
		user.setId(7L);
		user.setCash("+", 5000);
		user.setRole("USER");
		user.setStatus("ACTIVE");
		return user;
	}

	public static User Admin() {
		User user = new User(signupRequest(), "ePassword");
		user.setId(1L);
		user.setAdmin();
		return user;
	}

	public static Cash cash() {
		return new Cash(user().getId(), chargingRequest());
	}

	public static List<Cash> cashList() {
		List<Cash> list = new ArrayList<>();
		boolean full = false;
		int i = 1;

		while (!full) {
			i++;
			Cash cash = new Cash(user().getId(), new ChargingRequest("msg" + i, 5000 + i));
			list.add(cash);
			if (list.size() == 6) {
				full = true;
			}
		}
		return list;
	}

	public static List<Cash> onlyOneData() {
		List<Cash> list = new ArrayList<>();
		list.add(cash());
		return list;
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

	public static BuyNumberRequest buyNumberRequest() {
		return new BuyNumberRequest(5);
	}

	public static StatisticalNumberRequest statisticalNumberRequest() {
		return new StatisticalNumberRequest(
			5,
			1000
		);
	}

	public static List<Integer> countList() {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 45; i++) {
			list.add(1);
		}
		return list;
	}

	public static Stream<Arguments> statusTestData() {
		return Stream.of(
			Arguments.of(Status.SUSPENDED),
			Arguments.of(Status.DORMANT),
			Arguments.of(Status.TEST)
		);
	}

	public static Stream<Arguments> setPaidTestData() {
		return Stream.of(
			Arguments.of( 1000, UserRole.ROLE_USER),
			Arguments.of( 6000, UserRole.ROLE_PAID)
		);
	}

	public static Stream<Arguments> chargingTestData() {
		return Stream.of(
			Arguments.of(cashList()),
			Arguments.of(onlyOneData())
		);
	}

	public static Stream<Arguments> statisticalNumber() {
		return Stream.of(
			Arguments.of(20, 1000),
			Arguments.of(5, 2000)
		);
	}

}
