package com.example.sixnumber.user.service;

import static com.example.sixnumber.global.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.TestUtil;
import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.user.dto.CashNicknameResponse;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ChargingResponse;
import com.example.sixnumber.user.dto.MyInformationResponse;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.global.exception.StatusNotActiveException;
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

		when(userRepository.existsUserByEmail(anyString())).thenReturn(false);
		when(userRepository.existsUserByNickname(anyString())).thenReturn(false);

		String encodedPassword = "ePassword";
		when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn(encodedPassword);

		ApiResponse response = userService.signUp(signupRequest);

		verify(userRepository).existsUserByEmail(anyString());
		verify(userRepository).existsUserByNickname(anyString());
		verify(userRepository).save(any(User.class));
		assertEquals(201, response.getCode());
		assertEquals("회원가입 완료", response.getMsg());
	}

	@Test
	void signup_success_setActive() {
		SignupRequest request = TestDataFactory.signupRequest();

		saveUser.setStatus("DORMANT");

		when(userRepository.findByStatusAndEmail(eq(Status.DORMANT), anyString())).thenReturn(Optional.of(saveUser));

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		ApiResponse response = userService.signUp(request);

		verify(userRepository).findByStatusAndEmail(eq(Status.DORMANT), anyString());
		verify(passwordEncoder).matches(anyString(), anyString());
		verify(userRepository).save(any(User.class));
		assertEquals(saveUser.getStatus(), Status.ACTIVE);
		assertNull(saveUser.getWithdrawExpiration());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "재가입 완료");
	}

	@Test
	void signup_EmailOverlapException() {
		SignupRequest signupRequest = TestDataFactory.signupRequest();

		when(userRepository.existsUserByEmail(anyString())).thenReturn(true);

		Exception exception = assertThrows(OverlapException.class,
			() -> userService.signUp(signupRequest));

		verify(userRepository).existsUserByEmail(anyString());
		assertEquals(exception.getMessage(), "중복된 이메일입니다");
	}

	@Test
	void signup_NicknameOverlapException() {
		SignupRequest signupRequest = TestDataFactory.signupRequest();

		when(userRepository.existsUserByNickname(anyString())).thenReturn(true);

		Exception exception = assertThrows(OverlapException.class,
			() -> userService.signUp(signupRequest));

		verify(userRepository).existsUserByNickname(anyString());
		assertEquals(exception.getMessage(), "중복된 닉네임입니다");
	}

	@Test
	void signin_success() {
		SigninRequest signinRequest = TestDataFactory.signinRequest();

		when(manager.findUser(anyString())).thenReturn(saveUser);

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		when(jwtProvider.refreshToken(saveUser.getEmail(), saveUser.getId())).thenReturn("sampleRT");
		when(jwtProvider.accessToken(saveUser.getEmail(), saveUser.getId())).thenReturn("sampleAT");

		String Token = userService.signIn(signinRequest);

		verify(manager).findUser(anyString());
		verify(redisDao).overlapLogin(anyString());
		verify(passwordEncoder).matches(anyString(), anyString());
		verify(jwtProvider).refreshToken(saveUser.getEmail(), saveUser.getId());
		verify(jwtProvider).accessToken(saveUser.getEmail(), saveUser.getId());
		assertEquals(Token, "sampleAT,sampleRT");
		assertEquals(saveUser.getStatus(), Status.ACTIVE);
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
	void logout() {
		ApiResponse response = userService.logout(saveUser);

		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "로그아웃 성공");
		verify(redisDao).deleteValues(anyString());
	}

	@Test
	void withdraw_success() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("회원탈퇴");

		when(manager.findUser(anyString())).thenReturn(saveUser);

		ApiResponse response = userService.withdraw(request, saveUser.getEmail());

		verify(manager).findUser(anyString());
		assertEquals(saveUser.getStatus(), Status.DORMANT);
		assertNotNull(saveUser.getWithdrawExpiration());
		TestUtil.ApiAsserEquals(response, 200, "회원 탈퇴 완료");
	}

	@Test
	void withdraw_fail_incorrectMsg() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("incorrectMsg");

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.withdraw(request, saveUser.getEmail()));

		verify(request).getMsg();
	}

	@Test
	void setUser_success() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("월정액 해지");

		saveUser.setRole(UserRole.ROLE_PAID);
		saveUser.setPaymentDate(" ");

		when(manager.findUser(anyString())).thenReturn(saveUser);

		ApiResponse response = userService.setPaid(request, saveUser.getEmail());

		verify(manager).findUser(anyString());
		assertEquals(saveUser.getPaymentDate(), "월정액 해지");
		TestUtil.ApiAsserEquals(response, 200, "해지 신청 성공");
	}

	@Test
	void setUser_fail_USER() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("월정액 해지");

		// saveUser.getRole() = UserRole.USER
		when(manager.findUser(anyString())).thenReturn(saveUser);

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.setPaid(request, saveUser.getEmail()));

		verify(request).getMsg();
		verify(manager).findUser(anyString());
	}

	@Test
	void setPaid_success() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("false");

		when(manager.findUser(anyString())).thenReturn(saveUser);

		ApiResponse response = userService.setPaid(request, saveUser.getEmail());

		verify(manager).findUser(anyString());
		assertEquals(saveUser.getCash(), 1000);
		assertEquals(saveUser.getRole(), UserRole.ROLE_PAID);
		assertNotNull(saveUser.getPaymentDate());
		assertNotNull(saveUser.getStatement());
		TestUtil.ApiAsserEquals(response, 200, "권한 변경 성공");
	}

	@ParameterizedTest
	@MethodSource("com.example.sixnumber.fixture.TestDataFactory#setPaidTestData")
	void setPaid_fail_lowCash_Or_Role(int cash, UserRole role) {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("false");

		User user = mock(User.class);
		when(user.getEmail()).thenReturn("test@email.com");
		when(user.getCash()).thenReturn(cash);
		// 분명하게 필요한 정보인데 스터빙 오류가 계속 떠서 lenient() 적용함
		lenient().when(user.getRole()).thenReturn(role);

		when(manager.findUser(anyString())).thenReturn(user);

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.setPaid(request, user.getEmail()));

		verify(request).getMsg();
		verify(manager).findUser(anyString());
	}

	@Test
	void getCashNickname() {
		ItemApiResponse<CashNicknameResponse> response = userService.getCashNickname(saveUser);

		TestUtil.ItemApiAssertEquals(response, 200, "조회 성공");
	}

	@Test
	void charging_success() {
		ChargingRequest request = TestDataFactory.chargingRequest();

		Set<String> set = new HashSet<>(List.of("STMT: 7-1"));

		ApiResponse response = userService.charging(request, saveUser);

		verify(redisDao, times(2)).getKeysList(anyString());
		verify(redisDao).setValues(anyString(), anyString(), anyLong(), any());
		verify(userRepository).save(saveUser);
		assertEquals(saveUser.getChargingCount(), 1);
		TestUtil.ApiAsserEquals(response, 200, "요청 성공");
	}

	@Test
	void charging_fail_manyCharges() {
		ChargingRequest request = TestDataFactory.chargingRequest();

		Set<String> keys = TestDataFactory.keys();
		when(redisDao.getKeysList(anyString())).thenReturn(keys);

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.charging(request, saveUser));

		verify(redisDao).getKeysList(anyString());
	}

	@Test
	void charging_fail_KeyOverlapException() {
		ChargingRequest request = TestDataFactory.chargingRequest();

		Set<String> set = new HashSet<>(List.of("Msg-5000"));
		when(redisDao.getKeysList(anyString())).thenReturn(set);

		Assertions.assertThrows(OverlapException.class, () -> userService.charging(request, saveUser));

		verify(redisDao, times(2)).getKeysList(anyString());
	}

	@Test
	void charging_BreakTheRulesException() {
		ChargingRequest request = TestDataFactory.chargingRequest();

		saveUser.setChargingCount(4);

		Assertions.assertThrows(CustomException.class, () -> userService.charging(request, saveUser));
	}


	@Test
	void getCharges_success() {
		ListApiResponse<ChargingResponse> response = userService.getCharges(saveUser.getId());

		verify(redisDao).multiGet(anyString());
		TestUtil.ListApiAssertEquals(response, 200, "신청 리스트 조회 성공");
	}

	@Test
	void getStatement_success() {
		saveUser.setStatement(LocalDate.now() + ",5000" );

		when(manager.findUser(anyString())).thenReturn(saveUser);

		ListApiResponse<StatementResponse> response = userService.getStatement(saveUser.getEmail());

		verify(manager).findUser(anyString());
		assertEquals(response.getData().size(), 1);
		TestUtil.ListApiAssertEquals(response, 200, "거래내역 조회 완료");
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

		ApiResponse response = userService.update(request, saveUser);

		verify(passwordEncoder).matches(anyString(), anyString());
		verify(passwordEncoder).encode(anyString());
		TestUtil.ApiAsserEquals(response, 200, "수정 완료");
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

		ItemApiResponse<MyInformationResponse> response = userService.getMyInformation(saveUser);

		verify(manager).findUser(anyLong());
		TestUtil.ItemApiAssertEquals(response, 200, "조회 성공");
	}

	@Test
	void checkPW_success() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("ePassword");

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		ApiResponse response = userService.checkPW(request, saveUser.getPassword());

		verify(passwordEncoder).matches(anyString(), anyString());
		TestUtil.ApiAsserEquals(response, 200, "본인확인 성공");
	}

	@Test
	void checkPW_fail_incorrectPW() {
		OnlyMsgRequest request = mock(OnlyMsgRequest.class);
		when(request.getMsg()).thenReturn("false");

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		Assertions.assertThrows(IllegalArgumentException.class, ()->userService.checkPW(request, saveUser.getPassword()));

		verify(passwordEncoder).matches(anyString(), anyString());
	}
}
