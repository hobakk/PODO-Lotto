package com.example.sixnumber.board;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.sixnumber.board.dto.BoardRequest;
import com.example.sixnumber.board.service.BoardService;
import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.WithCustomMockUser;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.lotto.controller.LottoController;
import com.example.sixnumber.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@WebMvcTest(LottoController.class)
@WithCustomMockUser
public class BoardControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BoardService boardService;

	@Test
	@WithCustomMockUser
	public void setBoard() throws Exception {
		when(boardService.setBoard(any(BoardRequest.class), any(User.class)))
			.thenReturn(UnifiedResponse.ok("생성 완료"));

		mockMvc.perform(post("/api/board").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.boardRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("생성 완료"));
	}
}
