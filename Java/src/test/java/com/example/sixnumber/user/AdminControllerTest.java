package com.example.sixnumber.user;

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
import com.example.sixnumber.fixture.WithCustomMockUser;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.user.controller.AdminController;
import com.example.sixnumber.user.dto.AdminGetChargingResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.UserResponse;
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
	public void getUsers() throws Exception {
		UserResponse userResponse = new UserResponse(TestDataFactory.user());

		when(adminService.getUsers()).thenReturn(UnifiedResponse.ok("조회 성공", List.of(userResponse)));

		mockMvc.perform(get("/api/admin/users").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(adminService).getUsers();
	}

	@Test
	public void getCharges() throws Exception {
		AdminGetChargingResponse chargeList = TestDataFactory.adminGetChargingResponse();

		when(adminService.getCharges()).thenReturn(UnifiedResponse.ok("조회 성공", List.of(chargeList)));

		mockMvc.perform(get("/api/admin/charges").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(adminService).getCharges();
	}

	@Test
	public void searchCharge() throws Exception {
		AdminGetChargingResponse response = TestDataFactory.adminGetChargingResponse();

		when(adminService.searchCharging(anyString(), anyInt())).thenReturn(UnifiedResponse.ok("조회 성공", response));

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
	public void setAdmin() throws Exception {
		when(adminService.setAdmin(any(OnlyMsgRequest.class), any(User.class), anyLong()))
			.thenReturn(UnifiedResponse.ok("변경 완료"));

		mockMvc.perform(patch("/api/admin/users/99").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString("AdminSecurityKey")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("변경 완료"));

		verify(adminService).setAdmin(any(OnlyMsgRequest.class), any(User.class), anyLong());
	}

	@Test
	public void upCash() throws Exception {
		when(adminService.upCash(any(CashRequest.class))).thenReturn(UnifiedResponse.ok("충전 완료"));

		mockMvc.perform(patch("/api/admin/users/up-cash").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.cashRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("충전 완료"));

		verify(adminService).upCash(any(CashRequest.class));
	}

	@Test
	public void downCash() throws Exception {
		when(adminService.downCash(any(CashRequest.class))).thenReturn(UnifiedResponse.ok("차감 완료"));

		mockMvc.perform(patch("/api/admin/users/down-cash").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.cashRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("차감 완료"));

		verify(adminService).downCash(any(CashRequest.class));
	}

	@Test
	public void createLotto() throws Exception {
		when(adminService.createLotto(anyString())).thenReturn(UnifiedResponse.ok("생성 완료"));

		mockMvc.perform(post("/api/admin/lotto").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString("testAdmin")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("생성 완료"));

		verify(adminService).createLotto(anyString());
	}

	@Test
	public void setStatus() throws Exception {
		when(adminService.setStatus(any(User.class), anyLong(), any(OnlyMsgRequest.class)))
			.thenReturn(UnifiedResponse.ok("상태 변경 완료"));

		mockMvc.perform(patch("/api/admin/status/99").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString("SUSPENDED")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("상태 변경 완료"));

		verify(adminService).setStatus(any(User.class), anyLong(), any(OnlyMsgRequest.class));
	}

	@Test
	public void setRole() throws Exception {
		when(adminService.setRole(any(User.class), anyLong(), any(OnlyMsgRequest.class)))
			.thenReturn(UnifiedResponse.ok("권한 변경 완료"));

		mockMvc.perform(patch("/api/admin/role/99").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString("PAID")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("권한 변경 완료"));

		verify(adminService).setRole(any(User.class), anyLong(), any(OnlyMsgRequest.class));
	}
}
