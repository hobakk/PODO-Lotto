// package com.example.sixnumber.token;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
//
// import javax.servlet.http.Cookie;
//
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// import com.example.sixnumber.fixture.TestDataFactory;
// import com.example.sixnumber.global.exception.CustomException;
// import com.example.sixnumber.global.util.JwtProvider;
// import com.example.sixnumber.global.util.Manager;
// import com.example.sixnumber.global.util.RedisDao;
// import com.example.sixnumber.user.entity.User;
//
// @ExtendWith(MockitoExtension.class)
// public class TokenServiceTest {
// 	@InjectMocks
// 	private TokenService tokenService;
//
// 	@Mock
// 	private JwtProvider jwtProvider;
// 	@Mock
// 	private Manager manager;
// 	@Mock
// 	private PasswordEncoder passwordEncoder;
// 	@Mock
// 	private RedisDao redisDao;
//
// 	private User saveUser;
// 	private Cookie cookie;
// 	private ReIssuanceRequest request;
//
// 	@BeforeEach
// 	public void setup() {
// 		saveUser = TestDataFactory.user();
// 		cookie = new Cookie("accessToken", "tokenValue");
// 		request = new ReIssuanceRequest(saveUser.getId(), saveUser.getEmail(), "tokenValue");
// 	}
//
// 	@Test
// 	void reIssuance_success() {
// 		when(manager.findUser(anyString())).thenReturn(saveUser);
//
// 		when(redisDao.getValue(anyString())).thenReturn(cookie.getValue());
//
// 		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
//
// 		when(jwtProvider.validateRefreshToken(anyString())).thenReturn(true);
// 		when(jwtProvider.accessToken(anyString())).thenReturn(cookie.getValue());
// 		when(jwtProvider.getRemainingTime(anyString())).thenReturn(3600000L);
// 		when(jwtProvider.createCookie(anyString(), anyString(), anyLong())).thenReturn(cookie);
//
// 		Cookie response = tokenService.reIssuance(request);
//
// 		verify(manager).findUser(anyString());
// 		verify(redisDao).getValue(anyString());
// 		verify(passwordEncoder).matches(anyString(), anyString());
// 		verify(jwtProvider).validateRefreshToken(anyString());
// 		verify(jwtProvider).accessToken(anyString());
// 		verify(jwtProvider).getRemainingTime(anyString());
// 		assertNotNull(response);
// 	}
//
// 	@Test
// 	void reIssuance_fail_incorrect() {
// 		when(manager.findUser(anyString())).thenReturn(saveUser);
//
// 		when(redisDao.getValue(anyString())).thenReturn(cookie.getValue());
//
// 		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
//
// 		Assertions.assertThrows(CustomException.class, () -> tokenService.reIssuance(request));
//
// 		verify(manager).findUser(anyString());
// 		verify(redisDao).getValue(anyString());
// 		verify(passwordEncoder).matches(anyString(), anyString());
// 	}
//
// 	@Test
// 	void reIssuance_fail_InValidRefreshToken() {
// 		when(manager.findUser(anyString())).thenReturn(saveUser);
//
// 		when(redisDao.getValue(anyString())).thenReturn(cookie.getValue());
//
// 		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
//
// 		when(jwtProvider.validateRefreshToken(anyString())).thenReturn(false);
//
// 		Assertions.assertThrows(CustomException.class, () -> tokenService.reIssuance(request));
//
// 		verify(manager).findUser(anyString());
// 		verify(redisDao).getValue(anyString());
// 		verify(passwordEncoder).matches(anyString(), anyString());
// 		verify(jwtProvider).validateRefreshToken(anyString());
// 	}
// }
