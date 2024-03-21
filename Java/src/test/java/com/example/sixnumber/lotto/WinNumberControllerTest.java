package com.example.sixnumber.lotto;

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
import com.example.sixnumber.lotto.controller.WinNumberController;
import com.example.sixnumber.lotto.dto.TransformResponse;
import com.example.sixnumber.lotto.dto.WinNumbersResponse;
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

	private WinNumbersResponse response;

	@BeforeEach
	public void setup() {
		response = new WinNumbersResponse(List.of(TestDataFactory.winNumber()));
	}

	@Test
	public void GetWinNumbers() throws Exception {
		when(winNumberService.getWinNumbers()).thenReturn(response);

		mockMvc.perform(get("/api/winnumber").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(winNumberService).getWinNumbers();
	}

	@Test
	public void SetWinNumber() throws Exception {
		when(winNumberService.setWinNumbers(anyInt())).thenReturn(response);

		mockMvc.perform(post("/api/winnumber/set").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.winNumberRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("등록 성공"));

		verify(winNumberService).setWinNumbers(anyInt());
	}
}
