package com.example.sixnumber.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.TestUtil;
import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.lotto.dto.SixNumberResponse;
import com.example.sixnumber.lotto.repository.SixNumberRepository;
import com.example.sixnumber.lotto.service.WinNumberService;
import com.example.sixnumber.user.dto.CashNicknameResponse;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ChargingResponse;
import com.example.sixnumber.user.dto.EmailAuthCodeRequest;
import com.example.sixnumber.user.dto.EmailRequest;
import com.example.sixnumber.user.dto.FindPasswordRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.StatementModifyMsgRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.dto.UserResponse;
import com.example.sixnumber.user.dto.UserResponseAndEncodedRefreshDto;
import com.example.sixnumber.user.dto.WinningNumberResponse;
import com.example.sixnumber.user.entity.Statement;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.StatementRepository;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.service.UserService;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;
	@Mock
	private StatementRepository statementRepository;
	@Mock
	private SixNumberRepository sixNumberRepository;
	@Mock
	private WinNumberService winNumberService;
	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private RedisDao redisDao;
	@Mock
	private Manager manager;

	private User saveUser;

	@BeforeEach
	public void setup() {
		saveUser = TestDataFactory.user();
	}

	@Test
	void sendAuthCodeToEmail_success() {
		EmailRequest emailRequest = TestDataFactory.emailRequest();
		Errors errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(false);

		UnifiedResponse<?> response = userService.sendAuthCodeToEmail(emailRequest, errors);

		verify(redisDao).setValues(anyString(), anyString(), anyLong(), any(TimeUnit.class));
		verify(manager).sendEmail(anyString(), anyString());
		TestUtil.UnifiedResponseEquals(response, 200, "인증번호 발급 성공");
	}

	@Test
	void sendAuthCodeToEmail_fail_ErrorsIsNotNull() {
		EmailRequest emailRequest = TestDataFactory.emailRequest();
		Errors errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(true);

		Assertions.assertThrows(OverlapException.class,
			() -> userService.sendAuthCodeToEmail(emailRequest, errors));
	}

	@Test
	void sendAuthCodeToEmail_fail_inCorrectEmailType() {
		EmailRequest emailRequest = new EmailRequest("test@false.com");
		Errors errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(false);

		Assertions.assertThrows(CustomException.class,
			() -> userService.sendAuthCodeToEmail(emailRequest, errors));
	}

	@Test
	void compareAuthCode_success() {
		EmailAuthCodeRequest emailAuthCodeRequest = mock(EmailAuthCodeRequest.class);
		when(emailAuthCodeRequest.getAuthCode()).thenReturn("123456");
		when(emailAuthCodeRequest.getEmail()).thenReturn("email");

		when(redisDao.getValue(anyString())).thenReturn(Optional.of("123456"));

		UnifiedResponse<?> response = userService.compareAuthCode(emailAuthCodeRequest);

		verify(redisDao).getValue(anyString());
		TestUtil.UnifiedResponseEquals(response, 200, "인증번호 일치");
	}

	@Test
	void compareAuthCode_fail_inCorrectAuthCode() {
		EmailAuthCodeRequest emailAuthCodeRequest = mock(EmailAuthCodeRequest.class);
		when(emailAuthCodeRequest.getEmail()).thenReturn("email");

		when(redisDao.getValue(anyString())).thenReturn(Optional.empty());

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.compareAuthCode(emailAuthCodeRequest));
	}

	@Test
	void signup_success() {
		SignupRequest signupRequest = TestDataFactory.signupRequest();
		Errors errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(false);

		when(userRepository.findByStatusAndEmail(any(Status.class), anyString())).thenReturn(Optional.empty());
		when(userRepository.existsUserByNickname(anyString())).thenReturn(false);

		when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("ePassword");

		UnifiedResponse<?> response = userService.signUp(signupRequest, errors);

		verify(userRepository).findByStatusAndEmail(any(Status.class), anyString());
		verify(userRepository).existsUserByNickname(anyString());
		verify(userRepository).save(any(User.class));
		TestUtil.UnifiedResponseEquals(response, 201, "회원가입 완료");
	}

	@Test
	void signup_success_ReJoin() {
		SignupRequest request = TestDataFactory.signupRequest();
		Errors errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(false);
		saveUser.setStatus(Status.DORMANT);

		when(userRepository.findByStatusAndEmail(eq(Status.DORMANT), anyString())).thenReturn(Optional.of(saveUser));

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		UnifiedResponse<?> response = userService.signUp(request, errors);

		verify(userRepository).findByStatusAndEmail(eq(Status.DORMANT), anyString());
		verify(passwordEncoder).matches(anyString(), anyString());
		verify(userRepository).save(any(User.class));
		assertEquals(saveUser.getStatus(), Status.ACTIVE);
		assertNull(saveUser.getWithdrawExpiration());
		TestUtil.UnifiedResponseEquals(response, 200, "재가입 완료");
	}

	@Test
	void signup_EmailOverlap() {
		SignupRequest signupRequest = TestDataFactory.signupRequest();
		Errors errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(false);

		when(userRepository.existsUserByEmail(anyString())).thenReturn(true);

		Exception exception = assertThrows(OverlapException.class,
			() -> userService.signUp(signupRequest, errors));

		verify(userRepository).existsUserByEmail(anyString());
		assertEquals(exception.getMessage(), "중복된 이메일입니다");
	}

	@Test
	void signup_fail_NicknameOverlap() {
		SignupRequest signupRequest = TestDataFactory.signupRequest();
		Errors errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(false);

		when(userRepository.existsUserByNickname(anyString())).thenReturn(true);

		Exception exception = assertThrows(OverlapException.class,
			() -> userService.signUp(signupRequest, errors));

		verify(userRepository).existsUserByNickname(anyString());
		assertEquals(exception.getMessage(), "중복된 닉네임입니다");
	}

	@Test
	void signin_success() {
		SigninRequest signinRequest = TestDataFactory.signinRequest();
		TokenDto tokenDto = TestDataFactory.tokenRequest();
		HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
		saveUser.setRefreshPointer(null);

		when(userRepository.findByEmailAndPasswordNotContainingAndStatus(anyString(), anyString(), any(Status.class)))
			.thenReturn(Optional.of(saveUser));

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		when(jwtProvider.generateTokens(any(User.class))).thenReturn(tokenDto);

		when(passwordEncoder.encode(anyString())).thenReturn("encodedRefreshToken");

		UnifiedResponse<?> response = userService.signIn(httpServletResponse, signinRequest);

		verify(userRepository).findByEmailAndPasswordNotContainingAndStatus(anyString(), anyString(), any(Status.class));
		verify(passwordEncoder).matches(anyString(), anyString());
		verify(jwtProvider).generateTokens(any(User.class));
		verify(redisDao).setValues(anyString(), anyString(), anyLong(), any(TimeUnit.class));
		verify(jwtProvider).addCookiesToHeaders(any(HttpServletResponse.class), any(TokenDto.class), any(Object.class));
		verify(passwordEncoder).encode(anyString());
		verify(httpServletResponse).addCookie(any(Cookie.class));
		verify(httpServletResponse).addHeader(anyString(), anyString());
		TestUtil.UnifiedResponseEquals(response, 200, "로그인 성공");
	}

	@Test
	void signin_userNotFound() {
		HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
		SigninRequest signinRequest = TestDataFactory.signinRequest();

		when(userRepository.findByEmailAndPasswordNotContainingAndStatus(anyString(), anyString(), any(Status.class)))
			.thenReturn(Optional.empty());

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.signIn(httpServletResponse, signinRequest));
	}

	@Test
	void signin_fail_inCorrectPassword() {
		HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
		SigninRequest signinRequest = TestDataFactory.signinRequest();

		when(userRepository.findByEmailAndPasswordNotContainingAndStatus(anyString(), anyString(), any(Status.class)))
			.thenReturn(Optional.of(saveUser));

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.signIn(httpServletResponse, signinRequest));

		verify(userRepository).findByEmailAndPasswordNotContainingAndStatus(anyString(), anyString(), any(Status.class));
		verify(passwordEncoder).matches(anyString(), anyString());
	}

	@Test
	void signin_fail_refreshPointerIsNotNull() {
		SigninRequest signinRequest = TestDataFactory.signinRequest();
		HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

		when(userRepository.findByEmailAndPasswordNotContainingAndStatus(anyString(), anyString(), any(Status.class)))
			.thenReturn(Optional.of(saveUser));

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		UnifiedResponse<?> response = userService.signIn(httpServletResponse, signinRequest);

		verify(userRepository).findByEmailAndPasswordNotContainingAndStatus(anyString(), anyString(), any(Status.class));
		verify(passwordEncoder).matches(anyString(), anyString());
		verify(redisDao).delete(anyString());
		TestUtil.UnifiedResponseEquals(response, 400, "중복 로그인입니다");
	}

	@Test
	void logout() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		TokenDto tokenDto = new TokenDto("accessTokenValue", "refreshTokenValue");

		when(jwtProvider.resolveTokens(request)).thenReturn(tokenDto);
		when(jwtProvider.getRemainingTime(anyString())).thenReturn((long) 3000);

		UnifiedResponse<?> unifiedResponse = userService.logout(request, response, saveUser);

		verify(redisDao).delete(anyString());
		verify(userRepository).save(saveUser);
		verify(redisDao).setBlackList(anyString(), anyLong());
		verify(jwtProvider).addCookiesToHeaders(any(HttpServletResponse.class), any(TokenDto.class), anyInt());
		TestUtil.UnifiedResponseEquals(unifiedResponse, 200, "로그아웃 성공");
	}

	@Test
	void withdraw_success() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("회원탈퇴");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		UnifiedResponse<?> response = userService.withdraw(request, saveUser.getEmail());

		verify(userRepository).findByEmail(anyString());
		assertEquals(saveUser.getStatus(), Status.DORMANT);
		assertNotNull(saveUser.getWithdrawExpiration());
		TestUtil.UnifiedResponseEquals(response, 200, "회원 탈퇴 완료");
	}

	@Test
	void withdraw_fail_incorrectMsg() {
		OnlyMsgRequest request = new OnlyMsgRequest("incorrectMsg");

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.withdraw(request, saveUser.getEmail()));
	}

	@Test
	void changeToUser_success() {
		saveUser.setRole(UserRole.ROLE_PAID);
		saveUser.setCancelPaid(false);

		when(userRepository.findByIdAndRoleAndCancelPaidFalseOrCancelPaidIsNull(anyLong(), any(UserRole.class)))
			.thenReturn(Optional.of(saveUser));

		UnifiedResponse<?> response = userService.changeToUser(saveUser.getId());

		verify(userRepository).findByIdAndRoleAndCancelPaidFalseOrCancelPaidIsNull(anyLong(), any(UserRole.class));
		assertEquals(saveUser.getCancelPaid(), true);
		TestUtil.UnifiedResponseEquals(response, 200, "해지 신청 성공");
	}

	@Test
	void changeToUser_fail() {
		when(userRepository.findByIdAndRoleAndCancelPaidFalseOrCancelPaidIsNull(anyLong(), any(UserRole.class)))
			.thenReturn(Optional.empty());

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.changeToUser(saveUser.getId()));
	}

	@Test
	void changeToPaid_success() {
		when(userRepository.findByIdAndCashGreaterThanEqualAndRoleNot(anyLong(), anyInt(), any(UserRole.class)))
			.thenReturn(Optional.of(saveUser));

		UnifiedResponse<?> response = userService.changeToPaid(saveUser.getId());

		verify(userRepository).findByIdAndCashGreaterThanEqualAndRoleNot(anyLong(), anyInt(), any(UserRole.class));
		assertEquals(saveUser.getCash(), 1000);
		assertEquals(saveUser.getRole(), UserRole.ROLE_PAID);
		assertNotNull(saveUser.getPaymentDate());
		assertNotNull(saveUser.getStatementList());
		TestUtil.UnifiedResponseEquals(response, 200, "권한 변경 성공");
	}

	@Test
	void changeToPaid_fail() {
		when(userRepository.findByIdAndCashGreaterThanEqualAndRoleNot(anyLong(), anyInt(), any(UserRole.class)))
			.thenReturn(Optional.empty());

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.changeToPaid(saveUser.getId()));

	}

	@Test
	void getCashAndNickname() {
		UnifiedResponse<CashNicknameResponse> response = userService.getCashAndNickname(saveUser);

		TestUtil.UnifiedResponseEquals(response, 200, "조회 성공", CashNicknameResponse.class);
	}

	@Test
	void charging_success() {
		ChargingRequest request = TestDataFactory.chargingRequest();

		when(redisDao.getKeysList(anyString())).thenReturn(new HashSet<>());

		UnifiedResponse<?> response = userService.charging(request, saveUser);

		verify(redisDao).getKeysList(anyString());
		verify(redisDao).setValues(anyString(), anyString(), anyLong(), any());
		verify(userRepository).save(saveUser);
		assertEquals(saveUser.getTimeoutCount(), 1);
		TestUtil.UnifiedResponseEquals(response, 200, "요청 성공");
	}

	@Test
	void charging_fail_manyTimeOut() {
		ChargingRequest request = TestDataFactory.chargingRequest();
		saveUser.setTimeoutCount(4);

		Assertions.assertThrows(CustomException.class, () -> userService.charging(request, saveUser));
	}

	@Test
	void charging_fail_isNotNullInRedis() {
		ChargingRequest request = TestDataFactory.chargingRequest();

		when(redisDao.getKeysList(anyString())).thenReturn(Set.of(TestDataFactory.chargeKey()));

		Exception exception = assertThrows(OverlapException.class,
			() -> userService.charging(request, saveUser));

		verify(redisDao).getKeysList(anyString());
		assertEquals(exception.getMessage(), "다른 문자로 재시도 해주세요");
	}

	@Test
	void getCharges_success() {
		when(redisDao.multiGet(anyString())).thenReturn(List.of(TestDataFactory.chargeKey()));

		UnifiedResponse<ChargingResponse> response = userService.getCharge(saveUser.getId());

		verify(redisDao).multiGet(anyString());
		TestUtil.UnifiedResponseEquals(response, 200, "충전 요청 조회 성공");
	}

	@Test
	void getCharge_fail() {
		when(redisDao.multiGet(anyString())).thenReturn(new ArrayList<>());

		Assertions.assertThrows(CustomException.class, () -> userService.getCharge(anyLong()));

		verify(redisDao).multiGet(anyString());
	}

	@Test
	void deleteCharge() {
		UnifiedResponse<?> response = userService.deleteCharge("str", saveUser);

		verify(redisDao).delete(anyString());
		verify(userRepository).save(any(User.class));
		TestUtil.UnifiedResponseEquals(response, 200, "충전 요청 삭제 성공");
	}

	@Test
	void update_success() {
		SignupRequest request = new SignupRequest("testE", "testP", "testN");

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("passwordE");

		UnifiedResponse<?> response = userService.update(request, saveUser);

		verify(userRepository).existsUserByEmail(anyString());
		verify(passwordEncoder).matches(anyString(), anyString());
		verify(passwordEncoder).encode(anyString());
		verify(userRepository).existsUserByNickname(anyString());
		TestUtil.UnifiedResponseEquals(response, 200, "수정 완료");
	}

	@Test
	void update_fail_incorrectValue() {
		SignupRequest request = TestDataFactory.signupRequest();
		User user = mock(User.class);
		when(user.getEmail()).thenReturn("test@gmail.com");
		when(user.getPassword()).thenReturn("password1!");
		when(user.getNickname()).thenReturn("nickname");

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.update(request, user));
	}

	@Test
	void getStatement_success() {
		saveUser.addStatement(TestDataFactory.statement());

		when(userRepository.findByIdAndStatementListNotNull(anyLong())).thenReturn(Optional.of(saveUser));

		UnifiedResponse<List<StatementResponse>> response = userService.getStatement(saveUser.getId());

		verify(userRepository).findByIdAndStatementListNotNull(anyLong());
		assertEquals(response.getData().size(), 1);
		TestUtil.UnifiedResponseListEquals(response, 200, "거래내역 조회 완료");
	}

	@Test
	void getStatement_fail_notFound() {
		when(userRepository.findByIdAndStatementListNotNull(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.getStatement(saveUser.getId()));
	}

	@Test
	void modifyStatementMsg_success() {
		StatementModifyMsgRequest request = TestDataFactory.statementModifyMsgRequest();
		Statement statement = TestDataFactory.statement();

		when(statementRepository.findByIdAndAfterLastMonth(anyLong(), any(LocalDate.class)))
			.thenReturn(Optional.of(statement));

		UnifiedResponse<?> response = userService.modifyStatementMsg(request);

		verify(statementRepository).findByIdAndAfterLastMonth(anyLong(), any(LocalDate.class));
		assertEquals(statement.getMsg(), request.getMsg());
		TestUtil.UnifiedResponseEquals(response, 200, "텍스트 수정 성공");
	}

	@Test
	void modifyStatementMsg_fail_notFound() {
		StatementModifyMsgRequest request = TestDataFactory.statementModifyMsgRequest();

		when(statementRepository.findByIdAndAfterLastMonth(anyLong(), any(LocalDate.class)))
			.thenReturn(Optional.empty());

		Assertions.assertThrows(CustomException.class, () -> userService.modifyStatementMsg(request));
	}

	@Test
	void getSixNumberList_success() {
		User user = mock(User.class);
		when(user.getSixNumberList()).thenReturn(List.of(TestDataFactory.sixNumber()));

		when(userRepository.findByIdAndSixNumberListNotNull(anyLong())).thenReturn(Optional.of(user));

		UnifiedResponse<List<SixNumberResponse>> response = userService.getSixNumberList(anyLong());

		verify(userRepository).findByIdAndSixNumberListNotNull(anyLong());
		TestUtil.UnifiedResponseListEquals(response, 200, "조회 성공");
	}

	@Test
	void getSixNumberList_fail_isEmpty() {
		when(userRepository.findByIdAndSixNumberListNotNull(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(CustomException.class, () -> userService.getSixNumberList(anyLong()));
	}

	@Test
	void comparePassword_success() {
		OnlyMsgRequest request = new OnlyMsgRequest("ePassword");

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		UnifiedResponse<?> response = userService.comparePassword(request, saveUser.getPassword());

		verify(passwordEncoder).matches(anyString(), anyString());
		TestUtil.UnifiedResponseEquals(response, 200, "본인확인 성공");
	}

	@Test
	void comparePassword_fail_incorrectPW() {
		OnlyMsgRequest request = new OnlyMsgRequest("false");

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		Assertions.assertThrows(IllegalArgumentException.class,
			()->userService.comparePassword(request, saveUser.getPassword()));

		verify(passwordEncoder).matches(anyString(), anyString());
	}

	@Test
	void getMyInformation() {
		UnifiedResponse<UserResponse> response = userService.getMyInformation(saveUser);

		TestUtil.UnifiedResponseEquals(response, 200, "조회 성공", UserResponse.class);
	}

	@Test
	void findPassword() {
		FindPasswordRequest request = TestDataFactory.findPasswordRequest();
		Errors errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(false);

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		UnifiedResponse<?> response = userService.findPassword(request, errors);

		verify(userRepository).findByEmail(anyString());
		verify(passwordEncoder).encode(anyString());
		TestUtil.UnifiedResponseEquals(response, 200, "비밀번호 설정 성공");
	}

	@Test
	void attendance_exceeded() {
		when(redisDao.getValue(anyString())).thenReturn(Optional.of("value"));

		UnifiedResponse<?> response = userService.attendance(saveUser);

		verify(redisDao).getValue(anyString());
		TestUtil.UnifiedResponseEquals(response, 400, "오늘 이미 출석하셨습니다");
	}

	@Test
	void attendance_success() {
		when(redisDao.getValue(anyString())).thenReturn(Optional.empty());

		UnifiedResponse<?> response = userService.attendance(saveUser);

		verify(redisDao).getValue(anyString());
		verify(redisDao).setValues(anyString(), anyString(), anyLong(), any(TimeUnit.class));
		verify(userRepository).save(any(User.class));
		assertEquals(response.getCode(), 200);
		assertNotNull(response.getMsg());
	}

	@Test
	void checkLottoWinLastWeek_success() {
		when(winNumberService.getFirstWinNumber()).thenReturn(TestDataFactory.winNumber());
		when(sixNumberRepository.findAllByUserIdAndBuyDateAfterAndBuyDateBefore(
			anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)
		)).thenReturn(List.of(TestDataFactory.sixNumber()));

		UnifiedResponse<List<WinningNumberResponse>> response = userService.checkLottoWinLastWeek(saveUser.getId());

		verify(winNumberService).getFirstWinNumber();
		verify(sixNumberRepository).findAllByUserIdAndBuyDateAfterAndBuyDateBefore(
			anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)
		);
		TestUtil.UnifiedResponseListEquals(response, 200, "당첨 이력 조회 성공");
	}

	@Test
	void checkLottoWinLastWeek_NotFound() {
		when(winNumberService.getFirstWinNumber()).thenReturn(TestDataFactory.winNumber());
		when(sixNumberRepository.findAllByUserIdAndBuyDateAfterAndBuyDateBefore(
			anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)
		)).thenReturn(new ArrayList<>());

		Assertions.assertThrows(CustomException.class,
			()->userService.checkLottoWinLastWeek(saveUser.getId()));

		verify(winNumberService).getFirstWinNumber();
		verify(sixNumberRepository).findAllByUserIdAndBuyDateAfterAndBuyDateBefore(
			anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)
		);
	}
}
