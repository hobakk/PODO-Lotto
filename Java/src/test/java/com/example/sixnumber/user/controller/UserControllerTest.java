package com.example.sixnumber.user.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.user.dto.CashNicknameResponse;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ChargingResponse;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
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
	@WithCustomMockUser
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
	@WithCustomMockUser
	public void	Withdraw() throws Exception {
		when(userService.withdraw(any(OnlyMsgRequest.class), anyString()))
			.thenReturn(ApiResponse.ok("회원 탈퇴 완료"));

		mockMvc.perform(patch("/api/users/withdraw").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(TestDataFactory.onlyMsgRequest()))
				.content(objectMapper.writeValueAsString("testUSer")))
				.andExpect(status().isOk());

		verify(userService).withdraw(any(OnlyMsgRequest.class), anyString());
	}

	@Test
	@WithCustomMockUser
	public void GetCashNickname() throws Exception {
		User user = TestDataFactory.user();

		when(userService.getCashNickname(any(User.class))).thenReturn(
			ItemApiResponse.ok("조회 성공", new CashNicknameResponse(user)));

		mockMvc.perform(get("/api/users/cash").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").value("조회 성공"))
				.andExpect(jsonPath("$.data").isNotEmpty());

		verify(userService).getCashNickname(any(User.class));
	}

	@Test
	@WithCustomMockUser
	public void GetCharges() throws Exception {
		ChargingResponse response = new ChargingResponse("7-홍길동전-2000");
		List<ChargingResponse> responses = List.of(response);

		when(userService.getCharges(any(User.class))).thenReturn(ListApiResponse.ok("신청 리스트 조회 성공", responses));

		mockMvc.perform(get("/api/users/charging").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.user())))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.msg").value("신청 리스트 조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());;

		verify(userService).getCharges(any(User.class));
	}

	@Test
	@WithCustomMockUser
	public void Charging() throws Exception {
		when(userService.charging(any(ChargingRequest.class), any(User.class)))
			.thenReturn(ApiResponse.ok("요청 성공"));

		mockMvc.perform(post("/api/users/charging").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.chargingRequest()))
			.content(objectMapper.writeValueAsString(TestDataFactory.user())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("요청 성공"));

		verify(userService).charging(any(ChargingRequest.class), any(User.class));
	}

	@Test
	@WithCustomMockUser
	public void setPaid() throws Exception {
		when(userService.setPaid(any(OnlyMsgRequest.class), anyString())).thenReturn(ApiResponse.ok("권한 변경 성공"));

		mockMvc.perform(patch("/api/users/paid").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.onlyMsgRequest()))
			.content(objectMapper.writeValueAsString(TestDataFactory.user().getEmail())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("권한 변경 성공"));

		verify(userService).setPaid(any(OnlyMsgRequest.class), anyString());
	}
}