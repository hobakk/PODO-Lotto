package com.example.sixnumber.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.WithCustomMockUser;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.user.dto.CashNicknameResponse;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ChargingResponse;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.dto.UserResponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.UserService;
import com.example.sixnumber.user.type.Status;
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
		when(userService.signUp(any(SignupRequest.class), any(Errors.class))).thenReturn(UnifiedResponse.create("회원가입 완료"));

		mockMvc.perform(post("/api/users/signup").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.signupRequest())))
			.andExpect(jsonPath("$.code").value(201))
			.andExpect(jsonPath("$.msg").value("회원가입 완료"));

		verify(userService).signUp(any(SignupRequest.class), any(Errors.class));
	}

	@Test
	@WithCustomMockUser(status = Status.DORMANT)
	public void Signup_ReJoin() throws Exception {
		when(userService.signUp(any(SignupRequest.class), any(Errors.class))).thenReturn(UnifiedResponse.ok("재가입 완료"));

		mockMvc.perform(post("/api/users/signup").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.signupRequest())))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.msg").value("재가입 완료"));

		verify(userService).signUp(any(SignupRequest.class), any(Errors.class));
	}

	@Test
	@WithMockUser
	public void Signin_success() throws Exception {
		when(userService.signIn(any(HttpServletResponse.class), any(SigninRequest.class)))
			.thenReturn(UnifiedResponse.ok("로그인 성공"));

		mockMvc.perform(post("/api/users/signin").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.signinRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("로그인 성공"));

		verify(userService).signIn(any(HttpServletResponse.class), any(SigninRequest.class));
	}

	@Test
	@WithMockUser
	public void Signin_fail() throws Exception {
		when(userService.signIn(any(HttpServletResponse.class), any(SigninRequest.class)))
			.thenReturn(UnifiedResponse.badRequest("중복 로그인입니다"));

		mockMvc.perform(post("/api/users/signin").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(TestDataFactory.signinRequest())))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").value("중복 로그인입니다"));

		verify(userService).signIn(any(HttpServletResponse.class), any(SigninRequest.class));
	}

	@Test
	@WithCustomMockUser
	public void Logout() throws Exception {
		Cookie access = new Cookie("accessToken", null);

		when(userService.logout(any(HttpServletRequest.class), any(User.class))).thenReturn(access);

		mockMvc.perform(post("/api/users/logout").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(99L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("로그아웃 성공"))
			.andExpect(cookie().value("accessToken", (String) null));

		verify(userService).logout(any(HttpServletRequest.class), any(User.class));
	}

	@Test
	@WithCustomMockUser
	public void	Withdraw() throws Exception {
		when(userService.withdraw(any(OnlyMsgRequest.class), anyString()))
			.thenReturn(UnifiedResponse.ok("회원 탈퇴 완료"));

		mockMvc.perform(patch("/api/users/withdraw").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString("testUSer")))
			.andExpect(status().isOk());

		verify(userService).withdraw(any(OnlyMsgRequest.class), anyString());
	}

	@Test
	@WithCustomMockUser
	public void GetCashNickname() throws Exception {
		User user = TestDataFactory.user();

		when(userService.getCashAndNickname(any(User.class))).thenReturn(
			UnifiedResponse.ok("조회 성공", new CashNicknameResponse(user)));

		mockMvc.perform(get("/api/users/cash").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(userService).getCashAndNickname(any(User.class));
	}

	@Test
	@WithCustomMockUser
	public void GetCharge() throws Exception {
		ChargingResponse response = new ChargingResponse("7-홍길동전-2000");

		when(userService.getCharge(anyLong())).thenReturn(UnifiedResponse.ok("신청 리스트 조회 성공", response));

		mockMvc.perform(get("/api/users/charging").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.msg").value("신청 리스트 조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());;

		verify(userService).getCharge(anyLong());
	}

	@Test
	@WithCustomMockUser
	public void Charging() throws Exception {
		when(userService.charging(any(ChargingRequest.class), any(User.class)))
			.thenReturn(UnifiedResponse.ok("요청 성공"));

		mockMvc.perform(post("/api/users/charging").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.user())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("요청 성공"));

		verify(userService).charging(any(ChargingRequest.class), any(User.class));
	}

	@Test
	@WithCustomMockUser
	public void setPremium_changeToUser() throws Exception {
		when(userService.changeToUser(any(User.class))).thenReturn(UnifiedResponse.ok("해지 신청 성공"));

		mockMvc.perform(patch("/api/users/premium").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(new OnlyMsgRequest("월정액 해지"))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("해지 신청 성공"));

		verify(userService).changeToUser(any(User.class));
	}

	@Test
	@WithCustomMockUser
	public void setPremium_changeToPaid() throws Exception {
		when(userService.changeToPaid(anyString())).thenReturn(UnifiedResponse.ok("권한 변경 성공"));

		mockMvc.perform(patch("/api/users/premium").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(new OnlyMsgRequest("normal"))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("권한 변경 성공"));

		verify(userService).changeToPaid(anyString());
	}

	@Test
	@WithCustomMockUser
	public void getStatement() throws Exception {
		StatementResponse response = new StatementResponse("2023-07-14,테스트");

		when(userService.getStatement(anyString()))
			.thenReturn(UnifiedResponse.ok("거래내역 조회 완료", List.of(response)));

		mockMvc.perform(get("/api/users/statement").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("거래내역 조회 완료"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(userService).getStatement(anyString());
	}

	@Test
	@WithCustomMockUser
	public void update() throws Exception {
		when(userService.update(any(SignupRequest.class), any(User.class)))
			.thenReturn(UnifiedResponse.ok("수정 완료"));

		mockMvc.perform(patch("/api/users/update").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.signupRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("수정 완료"));

		verify(userService).update(any(SignupRequest.class), any(User.class));
	}

	@Test
	@WithCustomMockUser
	public void getMyInformation() throws Exception {
		UserResponse response = new UserResponse(TestDataFactory.user());

		when(userService.getMyInformation(anyLong())).thenReturn(UnifiedResponse.ok("조회 성공", response));

		mockMvc.perform(get("/api/users/my-information").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"));

		verify(userService).getMyInformation(anyLong());
	}

	@Test
	@WithCustomMockUser
	public void comparePassword() throws Exception {
		when(userService.comparePassword(any(OnlyMsgRequest.class), anyString()))
			.thenReturn(UnifiedResponse.ok("본인확인 성공"));

		mockMvc.perform(post("/api/users/check-pw").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(new OnlyMsgRequest("password"))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("본인확인 성공"));

		verify(userService).comparePassword(any(OnlyMsgRequest.class), anyString());
	}

	@Test
	@WithCustomMockUser
	public void getBuySixNumberList() throws Exception {
		List<SixNumberResponse> response = List.of(new SixNumberResponse(TestDataFactory.sixNumber()));

		when(userService.getBuySixNumberList(anyLong())).thenReturn(UnifiedResponse.ok("조회 성공", response));

		mockMvc.perform(get("/api/users/sixnumber-list").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(anyLong())))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(userService).getBuySixNumberList(anyLong());
	}
}