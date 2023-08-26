// package com.example.sixnumber.token;
//
// import static org.mockito.Mockito.*;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import javax.servlet.http.Cookie;
// import javax.servlet.http.HttpServletRequest;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.example.sixnumber.fixture.TestDataFactory;
// import com.example.sixnumber.user.dto.MyInformationResponse;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// @WebMvcTest(TokenController.class)
// @WithMockUser
// public class TokenControllerTest {
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@MockBean
// 	private TokenService tokenService;
//
// 	private Cookie cookie;
//
// 	@BeforeEach
// 	public void setup() {
// 		cookie = new Cookie("accessToken", "bearer tokenValue");
// 	}
//
// 	@Test
// 	public void getInformationAfterCheckLogin() throws Exception {
// 		HttpServletRequest request = mock(HttpServletRequest.class);
// 		UserIfAndCookieResponse userIfAndCookieResponse =	new UserIfAndCookieResponse(
// 			new MyInformationResponse(TestDataFactory.user()), cookie);
//
// 		when(tokenService.getInformationAfterCheckLogin(request)).thenReturn(userIfAndCookieResponse);
//
// 		mockMvc.perform(post("/api/jwt/check/login").with(csrf())
// 			.contentType(MediaType.APPLICATION_JSON)
// 			.content(objectMapper.writeValueAsString(TestDataFactory.tokenRequest())))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.msg").value("조회 및 재발급 성공"))
// 			.andExpect(jsonPath("$.data").isNotEmpty());
//
// 		verify(tokenService).getInformationAfterCheckLogin(request);
// 	}
//
// 	@Test
// 	public void renewAccessToken() throws Exception {
// 		when(tokenService.renewAccessToken(anyString())).thenReturn(cookie);
//
// 		mockMvc.perform(post("/api/jwt/renew/access").with(csrf())
// 			.contentType(MediaType.APPLICATION_JSON)
// 			.content(objectMapper.writeValueAsString("refreshToken")))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.msg").value("AccessToken 재발급 성공"));
//
// 		verify(tokenService).renewAccessToken(anyString());
// 	}
// }
