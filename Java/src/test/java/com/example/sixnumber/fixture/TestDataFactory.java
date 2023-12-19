package com.example.sixnumber.fixture;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import com.example.sixnumber.board.dto.BoardRequest;
import com.example.sixnumber.board.dto.CommentRequest;
import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.entity.Comment;
import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.dto.StatisticalNumberRequest;
import com.example.sixnumber.lotto.dto.WinNumberRequest;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.lotto.entity.WinNumber;
import com.example.sixnumber.user.dto.AdminGetChargingResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.EmailRequest;
import com.example.sixnumber.user.dto.FindPasswordRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.StatementModifyMsgRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.entity.Statement;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

public class TestDataFactory {

	public static User user() {
		User user = new User(signupRequest(), "ePassword");
		user.setId(7L);
		user.plusCash(5000);
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

	public static FindPasswordRequest findPasswordRequest() {
		return new FindPasswordRequest(
			"test@gmail.com",
			"password1!"
		);
	}

	public static SigninRequest signinRequest() {
		return new SigninRequest(
			"test@gmail.com",
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

	public static Statement statement() {
		Statement statement = new Statement(user(), "테스트", 2000);
		statement.setId((long) 7);
		return statement;
	}

	public static StatementResponse statementResponse() { return new StatementResponse(statement()); }

	public static StatementModifyMsgRequest statementModifyMsgRequest() {
		return new StatementModifyMsgRequest((long) 7, "변경될 메세지");
	}

	public static String chargeKey() { return "7-콩쥐팥쥐-2000-12시 30분 33초"; }

	public static AdminGetChargingResponse adminGetChargingResponse() {
		return new AdminGetChargingResponse(chargeKey());
	}

	public static EmailRequest emailRequest() { return new EmailRequest("test@gmail.com"); }

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

	public static BoardRequest boardRequest() {
		return new BoardRequest("제목", "내용");
	}

	public static Board board() {
		return new Board(user(), boardRequest());
	}

	public static CommentRequest commentRequest() {
		return new CommentRequest(7L, "댓글");
	}

	public static Comment comment() {
		return new Comment(user(), board(), "댓글");
	}
}
