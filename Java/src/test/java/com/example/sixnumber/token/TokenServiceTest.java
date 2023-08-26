// package com.example.sixnumber.token;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
//
// import javax.servlet.http.HttpServletRequest;
//
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.example.sixnumber.fixture.TestDataFactory;
// import com.example.sixnumber.global.exception.CustomException;
// import com.example.sixnumber.global.util.JwtProvider;
// import com.example.sixnumber.global.util.Manager;
// import com.example.sixnumber.user.dto.CookiesResponse;
// import com.example.sixnumber.user.entity.User;
//
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.ExpiredJwtException;
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
//
// 	private User saveUser;
// 	private HttpServletRequest request;
// 	private CookiesResponse cookies;
//
// 	@BeforeEach
// 	public void setup() {
// 		saveUser = TestDataFactory.user();
// 		request = mock(HttpServletRequest.class);
// 		cookies = TestDataFactory.cookiesResponse();
// 	}
//
// 	@Test
// 	void getInformationAfterCheckLogin_ValidAccessToken() {
// 		when(jwtProvider.getTokenValueInCookie(request)).thenReturn(cookies);
//
// 		when(jwtProvider.validateToken(anyString())).thenReturn(true);
// 		when(jwtProvider.getTokenInUserId(anyString())).thenReturn(saveUser.getId());
//
// 		when(manager.findUser(anyLong())).thenReturn(saveUser);
//
// 		UserIfAndCookieResponse response = tokenService.getInformationAfterCheckLogin(request);
//
// 		verify(jwtProvider).getTokenValueInCookie(request);
// 		verify(jwtProvider).validateToken(anyString());
// 		verify(jwtProvider).getTokenInUserId(anyString());
// 		assertNull(response.getCookie());
// 		assertNotNull(response.getResponse());
// 	}
//
// 	@Test
// 	void getInformationAfterCheckLogin_InvalidAccessToken() {
// 		Claims claims = mock(Claims.class);
// 		when(claims.getSubject()).thenReturn("refreshTokenPointer");
//
// 		when(jwtProvider.getTokenValueInCookie(request)).thenReturn(cookies);
// 		when(jwtProvider.validateToken(anyString())).thenReturn(false);
// 		when(jwtProvider.isTokenExpired(anyString())).thenReturn(false);
// 		when(jwtProvider.getClaims(anyString())).thenReturn(claims);
// 		when(jwtProvider.accessToken(claims.getSubject())).thenReturn(cookies.getAccessCookie().getValue());
// 		when(jwtProvider.createCookie(anyString(), anyString())).thenReturn(cookies.getAccessCookie());
//
// 		when(manager.findUser(anyLong())).thenReturn(saveUser);
//
// 		UserIfAndCookieResponse response = tokenService.getInformationAfterCheckLogin(request);
//
// 		verify(jwtProvider).validateToken(anyString());
// 		verify(jwtProvider).isTokenExpired(anyString());
// 		verify(jwtProvider).getClaims(anyString());
// 		verify(jwtProvider).accessToken(anyString());
// 		verify(jwtProvider).createCookie(anyString(), anyString());
// 		verify(manager).findUser(anyLong());
// 		assertNotNull(response.getResponse());
// 		assertNotNull(response.getCookie());
// 	}
//
// 	@Test
// 	void getInformationAfterCheckLogin_Fail_CookiesIsNull() {
// 		when(jwtProvider.getTokenValueInCookie(request)).thenReturn(new CookiesResponse());
//
// 		Assertions.assertThrows(CustomException.class, ()->tokenService.getInformationAfterCheckLogin(request));
//
// 		verify(jwtProvider).getTokenValueInCookie(request);
// 	}
//
// 	@Test
// 	void getInformationAfterCheckLogin_fail_ExpiredJwtException() {
// 		when(jwtProvider.getTokenValueInCookie(request)).thenReturn(cookies);
// 		when(jwtProvider.validateToken(cookies.getAccessCookie().getValue())).thenReturn(false);
// 		when(jwtProvider.isTokenExpired(cookies.getRefreshCookie().getValue())).thenThrow(ExpiredJwtException.class);
//
// 		Assertions.assertThrows(CustomException.class,
// 			() -> tokenService.getInformationAfterCheckLogin(request));
//
// 		verify(jwtProvider).isTokenExpired(anyString());
// 	}
// }
