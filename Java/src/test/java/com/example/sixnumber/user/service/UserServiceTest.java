package com.example.sixnumber.user.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import com.example.sixnumber.global.exception.StatusNotActiveException;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.lotto.dto.SixNumberResponse;
import com.example.sixnumber.user.dto.CashNicknameResponse;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ChargingResponse;
import com.example.sixnumber.user.dto.CookieAndTokenResponse;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.dto.UserResponse;
import com.example.sixnumber.user.dto.UserResponseAndEncodedRefreshDto;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;
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
		// MockitoAnnotations.openMocks(this);
		saveUser = TestDataFactory.user();
	}

	@Test
	void signup_success() {
		SignupRequest signupRequest = TestDataFactory.signupRequest();
		Errors errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(false);

		when(userRepository.existsUserByEmail(anyString())).thenReturn(false);
		when(userRepository.existsUserByNickname(anyString())).thenReturn(false);

		String encodedPassword = "ePassword";
		when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn(encodedPassword);

		UnifiedResponse<?> response = userService.signUp(signupRequest, errors);

		verify(userRepository).existsUserByEmail(anyString());
		verify(userRepository).existsUserByNickname(anyString());
		verify(userRepository).save(any(User.class));
		TestUtil.UnifiedResponseEquals(response, 201, "회원가입 완료");
	}

	@Test
	void signup_success_setActive() {
		SignupRequest request = TestDataFactory.signupRequest();
		Errors errors = mock(Errors.class);

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
	void signup_EmailOverlapException() {
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
	void signup_NicknameOverlapException() {
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
	void signup_fail_ErrorsIsNotNull() {
		SignupRequest signupRequest = TestDataFactory.signupRequest();
		Errors errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(true);

		Assertions.assertThrows(CustomException.class, () -> userService.signUp(signupRequest, errors));
	}

	@Test
	void signin_success() {
		SigninRequest signinRequest = TestDataFactory.signinRequest();
		TokenDto tokenDto = TestDataFactory.tokenRequest();
		CookieAndTokenResponse cookieAndTokenResponse = TestDataFactory.cookiesResponse();

		when(manager.findUser(anyString())).thenReturn(saveUser);

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		when(jwtProvider.generateTokens(any(User.class))).thenReturn(tokenDto);
		when(jwtProvider.createCookie(anyString(), anyString(), anyString()))
			.thenReturn(cookieAndTokenResponse.getAccessCookie());

		String encodedRefreshToken = "EnCodedRefreshTokenValue";
		when(passwordEncoder.encode(anyString())).thenReturn(encodedRefreshToken);

		CookieAndTokenResponse response = userService.signIn(signinRequest);

		verify(manager).findUser(anyString());
		verify(passwordEncoder).matches(anyString(), anyString());
		verify(redisDao).getValue(anyString());
		verify(jwtProvider).generateTokens(any(User.class));
		verify(redisDao).setRefreshToken(anyString(), anyString(), anyLong(), any(TimeUnit.class));
		verify(jwtProvider).createCookie(anyString(), anyString(), anyString());
		verify(passwordEncoder).encode(anyString());
		assertNotNull(response.getAccessCookie());
		assertEquals(response.getEnCodedRefreshToken(), "Bearer " + encodedRefreshToken);
	}

	@Test
	void signin_fail_oauth2Login() {
		SigninRequest signinRequest = TestDataFactory.signinRequest();
		User user = mock(User.class);
		when(user.getPassword()).thenReturn("Oauth2Login");

		when(manager.findUser(anyString())).thenReturn(user);

		Assertions.assertThrows(CustomException.class, () -> userService.signIn(signinRequest));

		verify(manager).findUser(anyString());
	}

	@Test
	void signin_UserNotFoundException() {
		SigninRequest signinRequest = TestDataFactory.signinRequest();

		when(manager.findUser(anyString())).thenThrow(new CustomException(USER_NOT_FOUND));

		Assertions.assertThrows(CustomException.class, () -> userService.signIn(signinRequest));

		verify(manager).findUser(anyString());
	}

	@ParameterizedTest
	@MethodSource("com.example.sixnumber.fixture.TestDataFactory#statusTestData")
	void signin_fail_Status(Status status) {
		SigninRequest signinRequest = TestDataFactory.signinRequest();

		User user = mock(User.class);
		when(user.getStatus()).thenReturn(status);

		when(manager.findUser(anyString())).thenReturn(user);

		Assertions.assertThrows(StatusNotActiveException.class,
			() -> userService.signIn(signinRequest));

		verify(manager).findUser(anyString());
	}

	@Test
	void signin_fail_incorrectPW() {
		SigninRequest signinRequest = TestDataFactory.signinRequest();

		when(manager.findUser(anyString())).thenReturn(saveUser);

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.signIn(signinRequest));

		verify(manager).findUser(anyString());
		verify(passwordEncoder).matches(anyString(), anyString());
	}

	@Test
	void signin_fail_OverlapException() {
		SigninRequest signinRequest = TestDataFactory.signinRequest();

		when(manager.findUser(anyString())).thenReturn(saveUser);
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(redisDao.getValue(anyString())).thenReturn("notNull");

		Assertions.assertThrows(OverlapException.class, () -> userService.signIn(signinRequest));

		verify(manager).findUser(anyString());
		verify(redisDao).getValue(anyString());
		verify(redisDao).deleteValues(anyString(), anyString());
	}

	@Test
	void logout() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		CookieAndTokenResponse cookies = TestDataFactory.cookiesResponse();
		String accessToken = "accessTokenValue";
		Cookie access = new Cookie("accessToken", null);

		when(jwtProvider.getAccessTokenInCookie(request)).thenReturn(accessToken);
		when(jwtProvider.createCookie(anyString(), eq(null), anyInt())).thenReturn(access);

		Cookie cookie = userService.logout(request, saveUser);

		verify(redisDao).deleteValues(anyString(), eq(JwtProvider.REFRESH_TOKEN));
		verify(redisDao).setBlackList(cookies.getAccessCookie().getValue());
		verify(jwtProvider).createCookie(anyString(), eq(null), anyInt());
		assertNotNull(cookie);
	}

	@Test
	void withdraw_success() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("회원탈퇴");

		when(manager.findUser(anyString())).thenReturn(saveUser);

		UnifiedResponse<?> response = userService.withdraw(request, saveUser.getEmail());

		verify(manager).findUser(anyString());
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
	void setUser_success() {
		OnlyMsgRequest request = new OnlyMsgRequest("월정액 해지");

		saveUser.setRole(UserRole.ROLE_PAID);
		saveUser.setCancelPaid(false);

		when(manager.findUser(anyString())).thenReturn(saveUser);

		UnifiedResponse<?> response = userService.setPaid(request, saveUser.getEmail());

		verify(manager).findUser(anyString());
		assertEquals(saveUser.getCancelPaid(), true);
		TestUtil.UnifiedResponseEquals(response, 200, "해지 신청 성공");
	}

	@Test
	void setUser_fail_USER() {
		OnlyMsgRequest request = new OnlyMsgRequest("월정액 해지");

		// saveUser.getRole() = UserRole.USER
		when(manager.findUser(anyString())).thenReturn(saveUser);

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.setPaid(request, saveUser.getEmail()));

		verify(manager).findUser(anyString());
	}

	@Test
	void setPaid_success() {
		OnlyMsgRequest request = new OnlyMsgRequest("false");

		when(manager.findUser(anyString())).thenReturn(saveUser);

		UnifiedResponse<?> response = userService.setPaid(request, saveUser.getEmail());

		verify(manager).findUser(anyString());
		assertEquals(saveUser.getCash(), 1000);
		assertEquals(saveUser.getRole(), UserRole.ROLE_PAID);
		assertNotNull(saveUser.getPaymentDate());
		assertNotNull(saveUser.getStatement());
		TestUtil.UnifiedResponseEquals(response, 200, "권한 변경 성공");
	}

	@ParameterizedTest
	@MethodSource("com.example.sixnumber.fixture.TestDataFactory#setPaidTestData")
	void setPaid_fail_lowCash_Or_Role(int cash, UserRole role) {
		OnlyMsgRequest request = new OnlyMsgRequest("false");

		User user = mock(User.class);
		when(user.getEmail()).thenReturn("test@email.com");
		when(user.getCash()).thenReturn(cash);
		// 분명하게 필요한 정보인데 스터빙 오류가 계속 떠서 lenient() 적용함
		lenient().when(user.getRole()).thenReturn(role);

		when(manager.findUser(anyString())).thenReturn(user);

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.setPaid(request, user.getEmail()));

		verify(manager).findUser(anyString());
	}

	@Test
	void getCashNickname() {
		UnifiedResponse<CashNicknameResponse> response = userService.getCashNickname(saveUser);

		TestUtil.UnifiedResponseEquals(response, 200, "조회 성공", CashNicknameResponse.class);
	}

	@Test
	void charging_success() {
		ChargingRequest request = TestDataFactory.chargingRequest();

		UnifiedResponse<?> response = userService.charging(request, saveUser);

		verify(redisDao, times(1)).getKeysList(anyLong());
		verify(redisDao, times(1)).getKeysList(anyString());
		verify(redisDao).setValues(anyString(), anyString(), anyLong(), any());
		verify(userRepository).save(saveUser);
		assertEquals(saveUser.getTimeOutCount(), 1);
		TestUtil.UnifiedResponseEquals(response, 200, "요청 성공");
	}

	@Test
	void charging_fail_manyCharges() {
		ChargingRequest request = TestDataFactory.chargingRequest();

		Set<String> keys = TestDataFactory.keys();
		when(redisDao.getKeysList(anyLong())).thenReturn(keys);

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.charging(request, saveUser));

		verify(redisDao).getKeysList(anyLong());
	}

	@Test
	void charging_fail_KeyOverlapException() {
		ChargingRequest request = TestDataFactory.chargingRequest();
		Set<String> set = new HashSet<>(List.of("Msg-5000"));

		when(redisDao.getKeysList(anyLong())).thenReturn(Collections.emptySet());
		when(redisDao.getKeysList(anyString())).thenReturn(set);

		Assertions.assertThrows(OverlapException.class, () -> userService.charging(request, saveUser));

		verify(redisDao, times(1)).getKeysList(anyString());
	}

	@Test
	void charging_BreakTheRulesException() {
		ChargingRequest request = TestDataFactory.chargingRequest();

		saveUser.setTimeOutCount(4);

		Assertions.assertThrows(CustomException.class, () -> userService.charging(request, saveUser));
	}


	@Test
	void getCharges_success() {
		UnifiedResponse<List<ChargingResponse>> response = userService.getCharges(saveUser.getId());

		verify(redisDao).multiGet(anyLong());
		TestUtil.UnifiedResponseListEquals(response, 200, "신청 리스트 조회 성공");
	}

	@Test
	void getStatement_success() {
		saveUser.setStatement(LocalDate.now() + ",5000" );

		when(manager.findUser(anyString())).thenReturn(saveUser);

		UnifiedResponse<List<StatementResponse>> response = userService.getStatement(saveUser.getEmail());

		verify(manager).findUser(anyString());
		assertEquals(response.getData().size(), 1);
		TestUtil.UnifiedResponseListEquals(response, 200, "거래내역 조회 완료");
	}

	@Test
	void getStatement_fail_lowSize() {
		when(manager.findUser(anyString())).thenReturn(saveUser);

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.getStatement(saveUser.getEmail()));

		verify(manager).findUser(anyString());
	}

	@Test
	void update_success() {
		SignupRequest request = new SignupRequest("testE", "testP", "testN");

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("passwordE");

		UnifiedResponse<?> response = userService.update(request, saveUser);

		verify(passwordEncoder).matches(anyString(), anyString());
		verify(passwordEncoder).encode(anyString());
		TestUtil.UnifiedResponseEquals(response, 200, "수정 완료");
	}

	@Test
	void update_fail_incorrectValue() {
		SignupRequest request = mock(SignupRequest.class);
		when(request.getEmail()).thenReturn("test@email.com");
		when(request.getPassword()).thenReturn("ePassword");
		when(request.getNickname()).thenReturn("nickname");

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.update(request, saveUser));
	}

	@Test
	void getMyInformation() {
		when(manager.findUser(anyLong())).thenReturn(saveUser);

		UnifiedResponse<UserResponse> response = userService.getMyInformation(saveUser.getId());

		verify(manager).findUser(anyLong());
		TestUtil.UnifiedResponseEquals(response, 200, "조회 성공", UserResponse.class);
	}

	@Test
	void checkPW_success() {
		OnlyMsgRequest request = new OnlyMsgRequest("ePassword");

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		UnifiedResponse<?> response = userService.checkPW(request, saveUser.getPassword());

		verify(passwordEncoder).matches(anyString(), anyString());
		TestUtil.UnifiedResponseEquals(response, 200, "본인확인 성공");
	}

	@Test
	void checkPW_fail_incorrectPW() {
		OnlyMsgRequest request = new OnlyMsgRequest("false");

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		Assertions.assertThrows(IllegalArgumentException.class, ()->userService.checkPW(request, saveUser.getPassword()));

		verify(passwordEncoder).matches(anyString(), anyString());
	}

	@Test
	void getBuySixNumberList_success() {
		User user = mock(User.class);
		when(user.getSixNumberList()).thenReturn(List.of(TestDataFactory.sixNumber()));

		when(manager.findUser(anyLong())).thenReturn(user);

		UnifiedResponse<List<SixNumberResponse>> response = userService.getBuySixNumberList(anyLong());

		verify(manager).findUser(anyLong());
		TestUtil.UnifiedResponseListEquals(response, 200, "조회 성공");
	}

	@Test
	void getBuySixNumberList_fail_isEmpty() {
		User user = mock(User.class);
		when(user.getSixNumberList()).thenReturn(new ArrayList<>());

		when(manager.findUser(anyLong())).thenReturn(user);

		Assertions.assertThrows(CustomException.class, () -> userService.getBuySixNumberList(anyLong()));

		verify(manager).findUser(anyLong());
	}

	@Test
	void oauth2LoginAfterGetUserIfAndRefreshToken_success() {
		when(manager.findUser(anyLong())).thenReturn(saveUser);

		when(redisDao.getValue(anyString())).thenReturn("refreshT");

		when(passwordEncoder.encode(anyString())).thenReturn("encodedRefreshT");

		UserResponseAndEncodedRefreshDto dto = userService.oauth2LoginAfterGetUserIfAndRefreshToken(saveUser.getId());

		verify(manager).findUser(anyLong());
		verify(redisDao).getValue(anyString());
		verify(passwordEncoder).encode(anyString());
		assertNotNull(dto);
	}
}
