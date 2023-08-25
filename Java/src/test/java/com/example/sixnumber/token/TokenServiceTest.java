package com.example.sixnumber.token;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.user.dto.CookiesResponse;
import com.example.sixnumber.user.entity.User;

import io.jsonwebtoken.ExpiredJwtException;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {
	@InjectMocks
	private TokenService tokenService;

	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private Manager manager;

	private User saveUser;
	private TokenDto tokenDto;

	@BeforeEach
	public void setup() {
		saveUser = TestDataFactory.user();
		tokenDto = TestDataFactory.tokenRequest();
	}

	@Test
	void getInformationAfterCheckLogin_ValidAccessToken() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		CookiesResponse cookies = TestDataFactory.cookiesResponse();

		when(jwtProvider.getTokenValueInCookie(request)).thenReturn(cookies);

		when(jwtProvider.validateToken(anyString())).thenReturn(true);
		when(jwtProvider.getTokenInUserId(anyString())).thenReturn(saveUser.getId());

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		UserIfAndCookieResponse response = tokenService.getInformationAfterCheckLogin(request);

		verify(jwtProvider).getTokenValueInCookie(request);
		verify(jwtProvider).validateToken(anyString());
		verify(jwtProvider).getTokenInUserId(anyString());
		assertNull(response.getCookie());
		assertNotNull(response.getResponse());
	}

	@Test
	void getInformationAfterCheckLogin_Fail_CookiesIsNull() {
		HttpServletRequest request = mock(HttpServletRequest.class);

		when(jwtProvider.getTokenValueInCookie(request)).thenReturn(new CookiesResponse());

		Assertions.assertThrows(CustomException.class, ()->tokenService.getInformationAfterCheckLogin(request));

		verify(jwtProvider).getTokenValueInCookie(request);
	}

	@Test
	void getInformationAfterCheckLogin_InvalidAccessToken() {
		String[] idEmail = {"7", "anyString"};

		when(jwtProvider.validateToken(anyString())).thenReturn(false);
		when(jwtProvider.validateRefreshToken(anyString())).thenReturn(idEmail);
		when(jwtProvider.accessToken(anyString())).thenReturn("tokenValue");
		when(jwtProvider.getTokenInUserId(anyString())).thenReturn(saveUser.getId());

		when(manager.findUser(anyLong())).thenReturn(saveUser);

		UserIfAndCookieResponse response = tokenService.getInformationAfterCheckLogin(tokenDto);

		verify(jwtProvider).validateToken(anyString());
		verify(jwtProvider).validateRefreshToken(anyString());
		verify(jwtProvider).accessToken(anyString());
		verify(jwtProvider).getTokenInUserId(anyString());
		verify(manager).findUser(anyLong());
		assertNotNull(response);
	}

	@Test
	void getInformationAfterCheckLogin_fail_isNull() {
		String[] idEmail = {"anyLong"};

		when(jwtProvider.validateRefreshToken(anyString())).thenReturn(idEmail);

		Assertions.assertThrows(IllegalArgumentException.class,
			() -> tokenService.getInformationAfterCheckLogin(tokenDto));

		verify(jwtProvider).validateRefreshToken(anyString());
	}

	@Test
	void getInformationAfterCheckLogin_fail_ExpiredJwtException() {
		when(jwtProvider.validateRefreshToken(anyString())).thenThrow(ExpiredJwtException.class);

		Assertions.assertThrows(CustomException.class,
			() -> tokenService.getInformationAfterCheckLogin(tokenDto));

		verify(jwtProvider).validateRefreshToken(anyString());
	}
}
