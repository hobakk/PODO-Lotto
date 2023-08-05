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
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.user.dto.UsersReponse;
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
		List<UsersReponse> userList = List.of(usersReponse);

		when(adminService.getUsers()).thenReturn(ListApiResponse.ok("조회 성공", userList));

		mockMvc.perform(get("/api/admin/users").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(adminService).getUsers();
	}
}
