package com.example.sixnumber.lotto.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;

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
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.service.LottoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@WebMvcTest(LottoController.class)
@WithCustomMockUser
public class LottoControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private LottoService lottoService;
	private LottoResponse response;

	@BeforeEach
	public void setup() {
		response = new LottoResponse(TestDataFactory.countList(), "1 2 3 4 5 6");
	}

	@Test
	public void MainTopNumbers() throws Exception {
		when(lottoService.mainTopNumbers()).thenReturn(response);

		mockMvc.perform(get("/api/lotto/main").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(lottoService).mainTopNumbers();
	}

	@Test
	public void GetTopNumberForMonth() throws Exception {
		when(lottoService.getTopNumberForMonth(any(YearMonth.class))).thenReturn(response);

		mockMvc.perform(get("/api/lotto/yearMonth").with(csrf())
			.param("yearMonth","2023-07")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(lottoService).getTopNumberForMonth(any(YearMonth.class));
	}
}
