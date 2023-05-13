package com.example.sixnumber.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import com.example.sixnumber.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
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

	@InjectMocks
	private UserService userService;

	@BeforeEach
	public void setup() {
		// MockitoAnnotations.openMocks(this);
	}

	@Test
	void signup() {
		SignupRequest request = TestDataFactory.signupRequest();

		when(userRepository.existsUserByEmail(anyString())).thenReturn(false);
		when(userRepository.existsUserByNickname(anyString())).thenReturn(false);

		String encodedPassword = "ePassword";
		when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);

		User saveUser = new User(request, encodedPassword);
		when(userRepository.save(any(User.class))).thenReturn(saveUser);

		ApiResponse response = userService.signUp(request);

		verify(userRepository).existsUserByEmail(anyString());
		verify(userRepository).existsUserByNickname(anyString());
		verify(userRepository).save(any(User.class));
		assertEquals(201, response.getCode());
		assertEquals("회원가입 완료", response.getMsg());
	}

	@Test
	void signin() {
		SigninRequest signinRequest = TestDataFactory.signinRequest();

		User saveUser = TestDataFactory.user();

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
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
	void logout() {
		User saveUser = TestDataFactory.user();

		ApiResponse response = userService.logout(saveUser);

		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "로그아웃 성공");
		verify(redisTemplate).delete(anyString());
	}

	@Test
	void withdraw() {
		WithdrawRequest request = mock(WithdrawRequest.class);
		User saveUser = TestDataFactory.user();

		when(request.getMsg()).thenReturn("회원탈퇴");
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(saveUser));

		ApiResponse response = userService.withdraw(request, saveUser.getEmail());

		verify(redisTemplate).delete(anyString());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "회원 탈퇴 완료");
	}

	@Test
	void setUSER() {
		ReleasePaidRequest request = mock(ReleasePaidRequest.class);
		when(request.getMsg()).thenReturn("월정액 해지");

		User user = mock(User.class);
		when(user.getEmail()).thenReturn("email@test.com");
		when(user.getRole()).thenReturn(UserRole.ROLE_PAID);

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		ApiResponse response = userService.setPaid(request, user.getEmail());

		verify(user).getRole();
		verify(user).setPaymentDate(request.getMsg());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "해지 신청 성공");

	}

	@Test
	void setPAID() {
		ReleasePaidRequest request = mock(ReleasePaidRequest.class);
		when(request.getMsg()).thenReturn("false");

		User user = mock(User.class);
		when(user.getEmail()).thenReturn("email@test.com");
		when(user.getRole()).thenReturn(UserRole.ROLE_USER);
		when(user.getCash()).thenReturn(6000);

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		ApiResponse response = userService.setPaid(request, user.getEmail());

		verify(user).getCash();
		verify(user).getRole();
		verify(user).setCash("-", 5000);
		verify(user).setRole("PAID");
		verify(user).setPaymentDate(anyString());
		assertEquals(response.getCode(), 200);
		assertEquals(response.getMsg(), "권한 변경 성공");
	}

	@Test
	void getCash() {
		User user = TestDataFactory.user();

		int response = userService.getCash(user);

		assertEquals(user.getCash(), response);
	}

	@Test
	void Charginh() {
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
