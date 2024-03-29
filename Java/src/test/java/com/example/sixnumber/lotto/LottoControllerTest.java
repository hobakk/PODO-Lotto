package com.example.sixnumber.lotto;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.YearMonth;
import java.util.List;

import com.example.sixnumber.global.dto.UnifiedResponse;
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
import com.example.sixnumber.lotto.controller.LottoController;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthResponse;
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
	public void createLotto() throws Exception {
		when(lottoService.createLotto()).thenReturn(UnifiedResponse.ok("생성 완료"));

		mockMvc.perform(post("/api/admin/lotto").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString("testAdmin")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").value("생성 완료"));

		verify(lottoService).createLotto();
	}
	@Test
	public void MainTopNumbers() throws Exception {
		when(lottoService.mainTopNumbers()).thenReturn(response);

		mockMvc.perform(get("/api/lotto/main").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(lottoService).mainTopNumbers();
	}

	@Test
	public void GetTopNumberForMonth() throws Exception {
		when(lottoService.getMonthlyStats(any(YearMonth.class))).thenReturn(response);

		mockMvc.perform(get("/api/lotto/yearMonth").with(csrf())
			.param("yearMonth","2023-07")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(lottoService).getMonthlyStats(any(YearMonth.class));
	}

	@Test
	public void GetAllMonthStats() throws Exception {
		when(lottoService.getAllMonthlyStats()).thenReturn(new YearMonthResponse(List.of("2023-07")));

		mockMvc.perform(get("/api/lotto/yearMonth/all").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());

		verify(lottoService).getAllMonthlyStats();
	}
}
