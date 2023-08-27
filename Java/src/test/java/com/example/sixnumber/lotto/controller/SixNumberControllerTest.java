package com.example.sixnumber.lotto.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.dto.SixNumberResponse;
import com.example.sixnumber.lotto.dto.StatisticalNumberRequest;
import com.example.sixnumber.lotto.service.SixNumberService;
import com.example.sixnumber.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@WebMvcTest(SixNumberController.class)
@WithCustomMockUser
public class SixNumberControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private SixNumberService sixNumberService;
	private List<String> topNumbers;

	@BeforeEach
	public void setup() {
		topNumbers = List.of("1 2 3 4 5 6");
	}

	@Test
	public void BuyNumbers() throws Exception {
		when(sixNumberService.buyNumber(any(BuyNumberRequest.class), any(User.class))).thenReturn(
			UnifiedResponse.ok("요청 성공", topNumbers));

		mockMvc.perform(post("/api/sixnum").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.buyNumberRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("요청 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(sixNumberService).buyNumber(any(BuyNumberRequest.class), any(User.class));
	}

	@Test
	public void StatisticalNumber() throws Exception {
		when(sixNumberService.statisticalNumber(any(StatisticalNumberRequest.class), any(User.class))).thenReturn(
			UnifiedResponse.ok("요청 성공", topNumbers));

		mockMvc.perform(post("/api/sixnum/repetition").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.statisticalNumberRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("요청 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(sixNumberService).statisticalNumber(any(StatisticalNumberRequest.class), any(User.class));
	}

	@Test
	public void GetRecentBuyNumber() throws Exception {
		SixNumberResponse response = new SixNumberResponse(TestDataFactory.sixNumber());

		when(sixNumberService.getRecentBuyNumbers(any(User.class))).thenReturn(
			UnifiedResponse.ok("최근 구매 번호 조회 성공", response));

		mockMvc.perform(get("/api/sixnum/recent").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("최근 구매 번호 조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(sixNumberService).getRecentBuyNumbers(any(User.class));
	}
}
