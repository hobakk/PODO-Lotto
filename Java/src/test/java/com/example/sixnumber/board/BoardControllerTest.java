package com.example.sixnumber.board;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.sixnumber.board.dto.BoardRequest;
import com.example.sixnumber.board.dto.BoardResponse;
import com.example.sixnumber.board.dto.BoardsResponse;
import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.service.BoardService;
import com.example.sixnumber.board.type.BoardStatus;
import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.WithCustomMockUser;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.lotto.controller.LottoController;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.UserRole;
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

	@Test
	@WithCustomMockUser
	public void getBoardsByStatus() throws Exception {
		when(boardService.getBoardsByStatus(anyLong(), any(BoardStatus.class)))
			.thenReturn(UnifiedResponse.ok("조회 성공", List.of(new BoardsResponse(TestDataFactory.board()))));

		mockMvc.perform(get("/api/board").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());;
	}

	@Test
	@WithCustomMockUser
	public void getBoard() throws Exception {
		when(boardService.getBoard(any(User.class), anyLong()))
			.thenReturn(UnifiedResponse.ok("조회 성공", new BoardResponse(TestDataFactory.board())));

		mockMvc.perform(get("/api/board/1").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("조회 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());;
	}

	@Test
	@WithCustomMockUser
	public void deleteBoard() throws Exception {
		when(boardService.deleteBoard(any(User.class), anyLong()))
			.thenReturn(UnifiedResponse.ok("삭제 성공"));

		mockMvc.perform(delete("/api/board/1").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("삭제 성공"));
	}

	@Test
	@WithCustomMockUser
	public void fixBoard() throws Exception {
		when(boardService.updateBoard(any(User.class), anyLong(), any(BoardRequest.class)))
			.thenReturn(UnifiedResponse.ok("수정 성공"));

		mockMvc.perform(patch("/api/board/1").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestDataFactory.boardRequest())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("수정 성공"));
	}

	@Test
	@WithCustomMockUser(username = "testAdmin", role = UserRole.ROLE_ADMIN)
	public void getAllBoardsByStatus() throws Exception {
		Page<BoardsResponse> mockPage = new PageImpl<>(List.of(TestDataFactory.boardsResponse()));
		when(boardService.getAllBoardsByStatus(any(BoardStatus.class)))
			.thenReturn(UnifiedResponse.ok("조회 성공", mockPage));

		mockMvc.perform(patch("/api/board/1").with(csrf())
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("수정 성공"))
			.andExpect(jsonPath("$.data").isNotEmpty());;;
	}
}
