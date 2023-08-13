package com.example.sixnumber.token;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {
	@InjectMocks
	private TokenService tokenService;

	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private Manager manager;

	@Test
	void getInformationAfterCheckLogin_InvalidAccessToken() {
		User user = TestDataFactory.user();
		String[] idEmail = {"7", "anyString"};

		when(jwtProvider.validateToken(anyString())).thenReturn(false);
		when(jwtProvider.validateRefreshToken(anyString())).thenReturn(idEmail);
		when(jwtProvider.accessToken(anyString(), anyLong())).thenReturn("tokenValue");
		when(jwtProvider.getTokenInUserId(anyString())).thenReturn(user.getId());

		when(manager.findUser(anyLong())).thenReturn(user);

		UserIfAndCookieResponse response = tokenService.getInformationAfterCheckLogin(TestDataFactory.tokenRequest());

		verify(jwtProvider).validateToken(anyString());
		verify(jwtProvider).validateRefreshToken(anyString());
		verify(jwtProvider).accessToken(anyString(), anyLong());
		verify(jwtProvider).getTokenInUserId(anyString());
		verify(manager).findUser(anyLong());
		assertNotNull(response);
	}

	@Test
	void getInformationAfterCheckLogin_fail_isNull() {
		String[] idEmail = {"anyLong"};

		when(jwtProvider.validateRefreshToken(anyString())).thenReturn(idEmail);

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> tokenService.getInformationAfterCheckLogin(TestDataFactory.tokenRequest()));

		verify(jwtProvider).validateRefreshToken(anyString());
	}
}
