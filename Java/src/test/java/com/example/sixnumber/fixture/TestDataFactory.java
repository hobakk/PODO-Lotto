package com.example.sixnumber.fixture;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;

import org.junit.jupiter.params.provider.Arguments;

import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.dto.StatisticalNumberRequest;
import com.example.sixnumber.lotto.dto.WinNumberRequest;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.entity.WinNumber;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.CookieAndTokenResponse;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

public class TestDataFactory {

	public static User user() {
		User user = new User(signupRequest(), "ePassword");
		user.setId(7L);
		user.setCash("+", 5000);
		user.setRole(UserRole.ROLE_USER);
		user.setStatus(Status.ACTIVE);
		user.setRefreshPointer("refreshTokenPointer");
		return user;
	}

	public static User Admin() {
		User user = new User(signupRequest(), "ePassword");
		user.setId(1L);
		user.setAdmin();
		return user;
	}

	public static Lotto lotto() {
		return new Lotto("Stats", "lotto", YearMonth.of(2023,5), countList(), "1 2 3 4 5 6");
	}

	public static SixNumber sixNumber() { return new SixNumber(user(), LocalDateTime.now(), List.of("1 2 3 4 5 6")); }

	public static SignupRequest signupRequest() {
		return new SignupRequest(
			"test@gmail.com",
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
			"msg",
			5000
		);
	}

	public static WinNumberRequest winNumberRequest() {
		return new WinNumberRequest(
			"2023-07-11",
			1075,
			10000L,
			1,
			"1 2 3 4 5 6 7"
		);
	}

	public static OnlyMsgRequest onlyMsgRequest(String msg) {
		return new OnlyMsgRequest(msg);
	}

	public static WinNumber winNumber() {
		return  new WinNumber(winNumberRequest());
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

	public static CookieAndTokenResponse cookiesResponse() {
		Cookie access = new Cookie("accessToken", "accessTokenValue");
		String encodedRefreshToken = "EnCodedRefreshTokenValue";
		return new CookieAndTokenResponse(access, encodedRefreshToken);
	}

	public static Set<String> keys() {
		return new HashSet<>(List.of("Msg-5000", "Msg-50001", "Msg-50002"));
	}

	public static List<String> values() {
		return Arrays.asList("7-Msg-5000", "7-Msg-50001", "7-Msg-50002");
	}

	public static List<Integer> countList() {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 45; i++) {
			list.add(1);
		}
		return list;
	}

	public static TokenDto tokenRequest() {
		return new TokenDto("accessT", "refreshT", "refreshTPointer");
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

	public static Stream<Arguments> statisticalNumber() {
		return Stream.of(
			Arguments.of(20, 1000),
			Arguments.of(5, 2000)
		);
	}

	public static Stream<Arguments> cancellation() {
		User paid = user();
		paid.setRole(UserRole.ROLE_PAID);
		paid.setCancelPaid(false);

		User cancelPaidUser = user();
		cancelPaidUser.setRole(UserRole.ROLE_PAID);
		cancelPaidUser.setCancelPaid(true);

		return Stream.of(
			Arguments.of(paid, 1000, UserRole.ROLE_PAID, LocalDate.now().plusDays(31), false),
			Arguments.of(cancelPaidUser, 6000, UserRole.ROLE_USER, null, null)
		);
	}

}
