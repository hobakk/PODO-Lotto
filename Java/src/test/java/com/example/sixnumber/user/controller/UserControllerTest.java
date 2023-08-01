package com.example.sixnumber.user.controller;

import static com.example.sixnumber.global.util.JwtProvider.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.user.dto.SigninRequest;
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
	public void Signin() throws Exception {
		when(userService.signIn(any(SigninRequest.class))).thenReturn("AccessT,RefreshT");

		mockMvc.perform(MockMvcRequestBuilders
			.post("/api/users/signin")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.signinRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.msg").isString())
			.andExpect(header().string(AUTHORIZATION_HEADER, "Bearer AccessT"))
			.andExpect(header().exists("Set-Cookie"));

		verify(userService).signIn(any(SigninRequest.class));
	}
}