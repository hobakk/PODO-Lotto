package com.example.sixnumber.lotto.controller;

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
import com.example.sixnumber.lotto.dto.TransformResponse;
import com.example.sixnumber.lotto.dto.WinNumberResponse;
import com.example.sixnumber.lotto.service.WinNumberService;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@WebMvcTest(WinNumberController.class)
@WithCustomMockUser
public class WinNumberControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private WinNumberService winNumberService;

	@Test
	public void GetWinNumbers() throws Exception {
		TransformResponse transformResponse = new TransformResponse
			("2023-07-14", 14, 2000L, 7, TestDataFactory.countList(), 7);
		WinNumberResponse response = new WinNumberResponse(List.of(transformResponse));

		when(winNumberService.getWinNumbers()).thenReturn(response);

		mockMvc.perform(get("/api/winnumber").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(winNumberService).getWinNumbers();
	}
}
