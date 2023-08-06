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
import org.springframework.test.web.servlet.MockMvc;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.user.dto.AdminGetChargingResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.UsersReponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.AdminService;
import com.example.sixnumber.user.type.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@WebMvcTest(AdminController.class)
@WithCustomMockUser(username = "testAdmin", role = UserRole.ROLE_ADMIN)
public class AdminControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AdminService adminService;

	@Test
	public void GetUsers() throws Exception {
		UsersReponse usersReponse = new UsersReponse(TestDataFactory.user());

		when(adminService.getUsers()).thenReturn(ListApiResponse.ok("조회 성공", List.of(usersReponse)));

		mockMvc.perform(get("/api/admin/users").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(adminService).getUsers();
	}

	@Test
	public void GetCharges() throws Exception {
		AdminGetChargingResponse chargeList = new AdminGetChargingResponse("1-콩쥐팥쥐-2000");

		when(adminService.getCharges()).thenReturn(ListApiResponse.ok("조회 성공", List.of(chargeList)));

		mockMvc.perform(get("/api/admin/charges").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(adminService).getCharges();
	}

	@Test
	public void SearchCharging() throws Exception {
		AdminGetChargingResponse response = new AdminGetChargingResponse("1-콩쥐팥쥐-2000");

		when(adminService.searchCharging(anyString(), anyInt())).thenReturn(ItemApiResponse.ok("조회 성공", response));

		mockMvc.perform(get("/api/admin/search").with(csrf())
			.param("msg", "콩쥐팥쥐")
			.param("cash", "2000")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(adminService).searchCharging(anyString(), anyInt());
	}

	@Test
	public void SetAdmin() throws Exception {
		when(adminService.setAdmin(any(OnlyMsgRequest.class), any(User.class), anyLong()))
			.thenReturn(ApiResponse.ok("변경 완료"));

		mockMvc.perform(patch("/api/admin/users/99").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString("AdminSecurityKey")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("변경 완료"));

		verify(adminService).setAdmin(any(OnlyMsgRequest.class), any(User.class), anyLong());
	}

	@Test
	public void UpCash() throws Exception {
		when(adminService.upCash(any(CashRequest.class))).thenReturn(ApiResponse.ok("충전 완료"));

		mockMvc.perform(patch("/api/admin/users/up-cash").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.cashRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("충전 완료"));

		verify(adminService).upCash(any(CashRequest.class));
	}
}
