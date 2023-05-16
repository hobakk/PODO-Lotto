package com.example.sixnumber.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ReleasePaidRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.WithdrawRequest;
import com.example.sixnumber.user.entity.Cash;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.CashRepository;
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
	private CashRepository cashRepository;
	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private RedisTemplate<String, String> redisTemplate;

	private User saveUser;
	private SignupRequest signupRequest;
	private SigninRequest signinRequest;
	private ValueOperations<String, String> valueOperations;

	@BeforeEach
	public void setup() {
		// MockitoAnnotations.openMocks(this);
		saveUser = TestDataFactory.user();
		signupRequest = TestDataFactory.signupRequest();
		signinRequest = TestDataFactory.signinRequest();
		valueOperations = mock(ValueOperations.class);
	}

	@Test
	void signup_success() {
		when(userRepository.existsUserByEmail(anyString())).thenReturn(false);
		when(userRepository.existsUserByNickname(anyString())).thenReturn(false);

		String encodedPassword = "ePassword";
		when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn(encodedPassword);

		User saveUser = new User(signupRequest, encodedPassword);
		when(userRepository.save(any(User.class))).thenReturn(saveUser);

		ApiResponse response = userService.signUp(signupRequest);

		verify(userRepository).existsUserByEmail(anyString());
		verify(userRepository).existsUserByNickname(anyString());
		verify(userRepository).save(any(User.class));
		assertEquals(201, response.getCode());
		assertEquals("회원가입 완료", response.getMsg());
	}

	@Test
	void signup_fail_overlapEmail() {
		when(userRepository.existsUserByEmail(anyString())).thenReturn(true);

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.signUp(signupRequest));

		verify(userRepository).existsUserByEmail(anyString());
	}

	@Test
	void signup_fail_overlapNickname() {
		when(userRepository.existsUserByNickname(anyString())).thenReturn(true);

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.signUp(signupRequest));

		verify(userRepository).existsUserByNickname(anyString());
	}

	@Test
	void signin_success() {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(anyString())).thenReturn(null);

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		when(jwtProvider.refreshToken(saveUser.getEmail(), saveUser.getId())).thenReturn("sampleRT");
		when(jwtProvider.accessToken(saveUser.getEmail(), saveUser.getId())).thenReturn("sampleAT");

		String accessToken = userService.signIn(signinRequest);

		verify(userRepository).findByEmail(signinRequest.getEmail());
		verify(valueOperations).get(anyString());
		verify(valueOperations).set(anyString(), anyString());
		verify(passwordEncoder).matches(anyString(), anyString());
		verify(jwtProvider).refreshToken(saveUser.getEmail(), saveUser.getId());
		verify(jwtProvider).accessToken(saveUser.getEmail(), saveUser.getId());
		assertEquals(accessToken, "sampleAT");
	}

	@Test
	void signin_fail_overlapLogin() {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(anyString())).thenReturn("RTV");

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.signIn(signinRequest));

		verify(userRepository).findByEmail(anyString());
		verify(valueOperations).get(anyString());
		verify(redisTemplate).delete(anyString());
	}

	@ParameterizedTest
	@MethodSource("com.example.sixnumber.fixture.TestDataFactory#statusTestData")
	void signin_fail_Status(Status status) {
		User user = mock(User.class);
		when(user.getStatus()).thenReturn(status);

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(anyString())).thenReturn(null);

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.signIn(signinRequest));

		verify(userRepository).findByEmail(anyString());
		verify(valueOperations).get(anyString());
	}

	@Test
	void signin_fail_incorrectPW() {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(anyString())).thenReturn(null);

		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> userService.signIn(signinRequest));

		verify(userRepository).findByEmail(anyString());
		verify(valueOperations).get(anyString());
		verify(passwordEncoder).matches(anyString(), anyString());
	}

	@Test
	void logout() {
		User saveUser = TestDataFactory.user();

		ApiResponse response = userService.logout(saveUser);

		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "로그아웃 성공");
		verify(redisTemplate).delete(anyString());
	}

	@Test
	void withdraw_success() {
		WithdrawRequest request = mock(WithdrawRequest.class);
		when(request.getMsg()).thenReturn("회원탈퇴");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		ApiResponse response = userService.withdraw(request, saveUser.getEmail());

		verify(redisTemplate).delete(anyString());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "회원 탈퇴 완료");
	}

	@Test
	void withdraw_fail_incorrectMsg() {
		WithdrawRequest request = mock(WithdrawRequest.class);
		when(request.getMsg()).thenReturn("incorrectMsg");

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.withdraw(request, saveUser.getEmail()));

		verify(request).getMsg();
	}

	@Test
	void setUser_success() {
		ReleasePaidRequest request = mock(ReleasePaidRequest.class);
		when(request.getMsg()).thenReturn("월정액 해지");

		saveUser.setRole("PAID");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		ApiResponse response = userService.setPaid(request, saveUser.getEmail());

		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "해지 신청 성공");
	}

	@Test
	void setUser_fail_USER() {
		ReleasePaidRequest request = mock(ReleasePaidRequest.class);
		when(request.getMsg()).thenReturn("월정액 해지");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.setPaid(request, saveUser.getEmail()));

		verify(request).getMsg();
		verify(userRepository).findByEmail(anyString());
	}

	@Test
	void setPaid_success() {
		ReleasePaidRequest request = mock(ReleasePaidRequest.class);
		when(request.getMsg()).thenReturn("false");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		ApiResponse response = userService.setPaid(request, saveUser.getEmail());

		verify(userRepository).findByEmail(anyString());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "권한 변경 성공");
	}

	@ParameterizedTest
	@MethodSource("com.example.sixnumber.fixture.TestDataFactory#setPaidTestData")
	void setPaid_fail_lowCashOrRole(int cash, UserRole role) {
		ReleasePaidRequest request = mock(ReleasePaidRequest.class);
		when(request.getMsg()).thenReturn("false");

		User user = mock(User.class);
		when(user.getEmail()).thenReturn("test@email.com");
		when(user.getCash()).thenReturn(cash);
		// 분명하게 필요한 정보인데 스터빙 오류가 계속 떠서 lenient() 적용함
		lenient().when(user.getRole()).thenReturn(role);

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.setPaid(request, user.getEmail()));

		verify(request).getMsg();
		verify(userRepository).findByEmail(anyString());
	}

	@Test
	void getCash() {
		User user = TestDataFactory.user();

		int response = userService.getCash(user);

		assertEquals(user.getCash(), response);
	}

	@Test
	void Charging() {
		ChargingRequest request = mock(ChargingRequest.class);
		when(request.getMsg()).thenReturn("훈재오리");
		when(request.getValue()).thenReturn(5000);

		User saveUser = TestDataFactory.user();

		Cash cash = new Cash(saveUser.getId(), request);
		when(cashRepository.save(any(Cash.class))).thenReturn(cash);

		ApiResponse response = userService.charging(request, saveUser.getId());

		verify(cashRepository).save(any(Cash.class));
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "요청 성공");
	}
}
