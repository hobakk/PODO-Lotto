package com.example.sixnumber.board;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.sixnumber.board.dto.BoardResponse;
import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.repository.BoardRepository;
import com.example.sixnumber.board.service.BoardService;
import com.example.sixnumber.board.type.BoardStatus;
import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.TestUtil;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {
	@InjectMocks
	private BoardService boardService;

	@Mock
	private BoardRepository boardRepository;

	@Mock
	private Manager manager;
	private User saveUser;

	@BeforeEach
	public void setup() {
		saveUser = TestDataFactory.user();
	}

	@Test
	void setBoard_success() {
		when(boardRepository.findAllByUserIdAndStatus(anyLong(), any(BoardStatus.class)))
			.thenReturn(List.of(TestDataFactory.board()));

		UnifiedResponse<?> response = boardService.setBoard(TestDataFactory.boardRequest(), saveUser);

		verify(boardRepository).findAllByUserIdAndStatus(anyLong(), any(BoardStatus.class));
		verify(boardRepository).save(any(Board.class));
		TestUtil.UnifiedResponseEquals(response, 200, "생성 완료");
	}

	@Test
	void setBoard_fail() {
		List<Board> boardList = new ArrayList<>();
		for (int i = 0; i < 4; i++) boardList.add(TestDataFactory.board());

		when(boardRepository.findAllByUserIdAndStatus(anyLong(), any(BoardStatus.class))).thenReturn(boardList);

		Assertions.assertThrows(CustomException.class,
			() -> boardService.setBoard(TestDataFactory.boardRequest(), saveUser));

		verify(boardRepository).findAllByUserIdAndStatus(anyLong(), any(BoardStatus.class));
	}

	@Test
	void getBoardsByStatus() {
		when(boardRepository.findAllByUserIdAndStatus(anyLong(), any(BoardStatus.class)))
			.thenReturn(List.of(TestDataFactory.board()));

		UnifiedResponse<List<BoardResponse>> response = boardService
			.getBoardsByStatus(saveUser.getId(), BoardStatus.UNPROCESSED);

		verify(boardRepository).findAllByUserIdAndStatus(anyLong(), any(BoardStatus.class));
		TestUtil.UnifiedResponseListEquals(response, 200, "조회 성공");
	}

	@Test
	void getBoard_success() {
		when(boardRepository.findById(anyLong())).thenReturn(Optional.of(TestDataFactory.board()));

		UnifiedResponse<BoardResponse> response = boardService.getBoard(saveUser, TestDataFactory.board().getId());

		verify(boardRepository).findById(anyLong());
		TestUtil.UnifiedResponseEquals(response, 200, "조회 성공");
	}

	@Test
	void getBoard_fail() {
		User user = mock(User.class);
		when(user.getId()).thenReturn(91L);

		when(boardRepository.findById(anyLong())).thenReturn(Optional.of(TestDataFactory.board()));

		Assertions.assertThrows(CustomException.class,
			() -> boardService.getBoard(user, TestDataFactory.board().getId()));

		verify(boardRepository).findById(anyLong());
	}

	@Test
	void deleteBoard_isAdmin() {
		User admin = TestDataFactory.Admin();

		when(manager.isAdmin(any(User.class))).thenReturn(true);
		when(boardRepository.findById(anyLong())).thenReturn(Optional.of(TestDataFactory.board()));

		UnifiedResponse<?> response = boardService.deleteBoard(admin, TestDataFactory.board().getId());

		verify(manager).isAdmin(any(User.class));
		verify(boardRepository).findById(anyLong());
		verify(boardRepository).delete(any(Board.class));
		TestUtil.UnifiedResponseEquals(response, 200, "삭제 성공");
	}

	@Test
	void deleteBoard_isNotAdmin() {
		when(manager.isAdmin(any(User.class))).thenReturn(false);
		when(boardRepository.findByIdAndUserAndCommentList_Empty(anyLong(), any(User.class)))
			.thenReturn(Optional.of(TestDataFactory.board()));

		UnifiedResponse<?> response = boardService.deleteBoard(saveUser, TestDataFactory.board().getId());

		verify(manager).isAdmin(any(User.class));
		verify(boardRepository).findByIdAndUserAndCommentList_Empty(anyLong(), any(User.class));
		verify(boardRepository).delete(any(Board.class));
		TestUtil.UnifiedResponseEquals(response, 200, "삭제 성공");
	}

	@Test
	void fixBoard_success() {
		when(boardRepository.findByIdAndUser(anyLong(), any(User.class)))
			.thenReturn(Optional.of(TestDataFactory.board()));

		UnifiedResponse<?> response = boardService
			.fixBoard(saveUser, TestDataFactory.board().getId(), TestDataFactory.boardRequest());

		verify(boardRepository).findByIdAndUser(anyLong(), any(User.class));
		TestUtil.UnifiedResponseEquals(response, 200, "수정 성공");
	}
}
