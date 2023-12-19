package com.example.sixnumber.board;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.sixnumber.board.dto.CommentRequest;
import com.example.sixnumber.board.service.CommentService;
import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.WithCustomMockUser;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.lotto.controller.LottoController;
import com.example.sixnumber.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@WebMvcTest(LottoController.class)
@WithCustomMockUser
public class CommentControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CommentService commentService;
	private CommentRequest request;

	@BeforeEach
	public void setup() {
		request = TestDataFactory.commentRequest();
	}

	@Test
	@WithCustomMockUser
	public void setComment() throws Exception {
		when(commentService.setComment(any(User.class), any(CommentRequest.class)))
			.thenReturn(UnifiedResponse.ok("댓글 작성 완료"));

		mockMvc.perform(post("/api/comment").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("댓글 작성 완료"));
	}

	@Test
	@WithCustomMockUser
	public void fixComment() throws Exception {
		when(commentService.fixComment(any(User.class), any(CommentRequest.class)))
			.thenReturn(UnifiedResponse.ok("댓글 수정 성공"));

		mockMvc.perform(patch("/api/comment").with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value("댓글 수정 성공\""));
	}
}
