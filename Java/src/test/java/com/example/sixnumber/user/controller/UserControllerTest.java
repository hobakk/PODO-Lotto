package com.example.sixnumber.user.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.sixnumber.TestConfig;
import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
@Import(TestConfig.class)
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@Test
	@WithMockUser
	public void Signup() throws Exception {
		when(userService.signUp(any(SignupRequest.class))).thenReturn(ApiResponse.create("회원가입 완료"));

		mockMvc.perform(post("/api/users/signup").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.signupRequest())))
			.andExpect(jsonPath("$.code").value(201))
			.andExpect(jsonPath("$.msg").value("회원가입 완료"));

		verify(userService).signUp(any(SignupRequest.class));
	}

	@Test
	@WithMockUser
	public void Signin() throws Exception {
		when(userService.signIn(any(SigninRequest.class))).thenReturn("AccessT,RefreshT");

		mockMvc.perform(post("/api/users/signin").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.signinRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("로그인 성공"))
			.andExpect(cookie().value("accessToken", "AccessT"))
			.andExpect(cookie().value("refreshToken", "RefreshT"));

		verify(userService).signIn(any(SigninRequest.class));
	}

	@Test
	@WithMockUser
	public void Logout() throws Exception {
		when(userService.logout(any(User.class))).thenReturn(ApiResponse.ok("로그아웃 성공"));

		mockMvc.perform(post("/api/users/logout").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.user())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("로그아웃 성공"));

		verify(userService).logout(any(User.class));
	}

	@Test
	@WithUserDetails(value = "username", userDetailsServiceBeanName = "myUserDetailsService")
	public void	Withdraw() throws Exception {
		User user = TestDataFactory.user();

		when(userService.withdraw(any(OnlyMsgRequest.class), anyString()))
			.thenReturn(ApiResponse.ok("회원 탈퇴 완료"));

		mockMvc.perform(patch("/api/users/withdraw").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(TestDataFactory.onlyMsgRequest())))
				.andExpect(status().isOk());

		verify(userService).withdraw(any(OnlyMsgRequest.class), "asd");
	}

}