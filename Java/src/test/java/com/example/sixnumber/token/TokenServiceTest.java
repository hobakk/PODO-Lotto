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
import org.springframework.web.server.ResponseStatusException;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {
	@InjectMocks
	private TokenService tokenService;

	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private Manager manager;

	@Test
	void getInformationAfterCheckLogin_success() {
		when(jwtProvider.validateRefreshToken(anyString(), anyString())).thenReturn(true);
		when(jwtProvider.getIdEmail(anyString())).thenReturn("7,testEmail");
		when(jwtProvider.accessToken(anyString(), anyLong())).thenReturn("tokenValue");

		when(manager.findUser(anyString())).thenReturn(TestDataFactory.user());

		UserIfAndCookieResponse response = tokenService.getInformationAfterCheckLogin(TestDataFactory.tokenRequest());

		verify(jwtProvider).validateRefreshToken(anyString(), anyString());
		verify(jwtProvider).getIdEmail(anyString());
		verify(jwtProvider).accessToken(anyString(), anyLong());
		verify(manager).findUser(anyString());
		assertNotNull(response);
	}

	@Test
	void getInformationAfterCheckLogin_fail_Invalid() {
		when(jwtProvider.validateRefreshToken(anyString(), anyString())).thenReturn(false);

		Assertions.assertThrows(ResponseStatusException.class,
			() -> tokenService.getInformationAfterCheckLogin(TestDataFactory.tokenRequest()));

		verify(jwtProvider).validateRefreshToken(anyString(), anyString());
	}
}
