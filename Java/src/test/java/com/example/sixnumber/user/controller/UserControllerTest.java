package com.example.sixnumber.user.controller;

import static org.mockito.ArgumentMatchers.anyLong;
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
import com.example.sixnumber.fixture.WithCustomMockUser;
import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.user.dto.CashNicknameResponse;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ChargingResponse;
import com.example.sixnumber.user.dto.MyInformationResponse;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.UserService;
import com.example.sixnumber.user.type.Status;
import com.example.sixnumber.user.type.UserRole;
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
	@WithCustomMockUser(status = Status.DORMANT)
	public void Signup_ReJoin() throws Exception {
		when(userService.signUp(any(SignupRequest.class))).thenReturn(ApiResponse.ok("재가입 완료"));

		mockMvc.perform(post("/api/users/signup").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.signupRequest())))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.msg").value("재가입 완료"));

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
		when(userService.logout(anyLong())).thenReturn(ApiResponse.ok("로그아웃 성공"));

		mockMvc.perform(post("/api/users/logout").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(99L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("로그아웃 성공"));

		verify(userService).logout(anyLong());
	}

	@Test
	@WithCustomMockUser
	public void	Withdraw() throws Exception {
		when(userService.withdraw(any(OnlyMsgRequest.class), anyString()))
			.thenReturn(ApiResponse.ok("회원 탈퇴 완료"));

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

		when(userService.getCashNickname(any(User.class))).thenReturn(
			ItemApiResponse.ok("조회 성공", new CashNicknameResponse(user)));

		mockMvc.perform(get("/api/users/cash").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
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

		when(userService.getCharges(anyLong())).thenReturn(ListApiResponse.ok("신청 리스트 조회 성공", responses));

		mockMvc.perform(get("/api/users/charging").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.msg").value("신청 리스트 조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());;

		verify(userService).getCharges(anyLong());
	}

	@Test
	@WithCustomMockUser
	public void Charging() throws Exception {
		when(userService.charging(any(ChargingRequest.class), any(User.class)))
			.thenReturn(ApiResponse.ok("요청 성공"));

		mockMvc.perform(post("/api/users/charging").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.user())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("요청 성공"));

		verify(userService).charging(any(ChargingRequest.class), any(User.class));
	}

	@Test
	@WithCustomMockUser
	public void SetPaid() throws Exception {
		when(userService.setPaid(any(OnlyMsgRequest.class), anyString())).thenReturn(ApiResponse.ok("권한 변경 성공"));

		mockMvc.perform(patch("/api/users/paid").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.user().getEmail())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("권한 변경 성공"));

		verify(userService).setPaid(any(OnlyMsgRequest.class), anyString());
	}

	@Test
	@WithCustomMockUser(role = UserRole.ROLE_PAID)
	public void SetPaid_Release() throws Exception {
		when(userService.setPaid(any(OnlyMsgRequest.class), anyString())).thenReturn(ApiResponse.ok("해지 신청 성공"));

		mockMvc.perform(patch("/api/users/paid").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.user().getEmail())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("해지 신청 성공"));

		verify(userService).setPaid(any(OnlyMsgRequest.class), anyString());
	}

	@Test
	@WithCustomMockUser
	public void Update() throws Exception {
		when(userService.update(any(SignupRequest.class), any(User.class))).thenReturn(ApiResponse.ok("수정 완료"));

		mockMvc.perform(patch("/api/users/update").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.user())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("수정 완료"));

		verify(userService).update(any(SignupRequest.class), any(User.class));
	}

	@Test
	@WithCustomMockUser
	public void GetStatement() throws Exception {
		StatementResponse response = new StatementResponse(("2023-07-14,테스트").split(","));

		when(userService.getStatement(anyString())).thenReturn(ListApiResponse.ok("거래내역 조회 완료", List.of(response)));

		mockMvc.perform(get("/api/users/statement").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("거래내역 조회 완료"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(userService).getStatement(anyString());
	}

	@Test
	@WithCustomMockUser
	public void GetMyInformation() throws Exception {
		User user = TestDataFactory.user();
		MyInformationResponse response = new MyInformationResponse(user);

		when(userService.getMyInformation(anyLong())).thenReturn(ItemApiResponse.ok("조회 성공", response));

		mockMvc.perform(get("/api/users/my-information").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"));

		verify(userService).getMyInformation(anyLong());
	}

	@Test
	@WithCustomMockUser
	public void CheckPW() throws Exception {
		when(userService.checkPW(any(OnlyMsgRequest.class), anyString())).thenReturn(ApiResponse.ok("본인확인 성공"));

		mockMvc.perform(post("/api/users/check-pw").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString("password")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("본인확인 성공"));

		verify(userService).checkPW(any(OnlyMsgRequest.class), anyString());
	}
}