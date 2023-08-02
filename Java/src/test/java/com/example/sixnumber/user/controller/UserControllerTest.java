package com.example.sixnumber.user.controller;

import static com.example.sixnumber.global.util.JwtProvider.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@Test
	@WithMockUser(username = "test")
	public void Signup() throws Exception {
		when(userService.signUp(any(SignupRequest.class))).thenReturn(ApiResponse.ok("회원가입 완료"));

		mockMvc.perform(MockMvcRequestBuilders
			.post("/api/users/signup").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.signupRequest())))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("회원가입 완료"));

		verify(userService).signUp(any(SignupRequest.class));
	}

	@Test
	public void Signin() throws Exception {
		when(userService.signIn(any(SigninRequest.class))).thenReturn("AccessT,RefreshT");

		mockMvc.perform(MockMvcRequestBuilders
			.post("/api/users/signin")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.signinRequest())))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("로그인 성공"))
			.andExpect(header().string(AUTHORIZATION_HEADER, "Bearer AccessT"))
			.andExpect(header().exists("Set-Cookie"));

		verify(userService).signIn(any(SigninRequest.class));
	}
}