package com.example.sixnumber.board;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.sixnumber.board.dto.CommentRequest;
import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.entity.Comment;
import com.example.sixnumber.board.repository.BoardRepository;
import com.example.sixnumber.board.repository.CommentRepository;
import com.example.sixnumber.board.service.CommentService;
import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.fixture.TestUtil;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
	@InjectMocks
	private CommentService commentService;

	@Mock
	private BoardRepository boardRepository;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private Manager manager;
	private User saveUser;
	private CommentRequest commentRequest;

	@BeforeEach
	public void setup() {
		saveUser = TestDataFactory.user();
		commentRequest = TestDataFactory.commentRequest();
	}

	@Test
	void setComment_isNotAdmin() {
		when(manager.isAdmin(any(User.class))).thenReturn(false);
		when(boardRepository.findByIdAndCommentEnabled(anyLong(), anyBoolean()))
			.thenReturn(Optional.of(TestDataFactory.board()));

		UnifiedResponse<?> response = commentService.setComment(saveUser, commentRequest);

		verify(manager).isAdmin(any(User.class));
		verify(boardRepository).findByIdAndCommentEnabled(anyLong(), anyBoolean());
		verify(commentRepository).save(any(Comment.class));
		TestUtil.UnifiedResponseEquals(response, 200, "댓글 작성 완료");
	}

	@Test
	void setComment_isAdmin() {
		User admin = TestDataFactory.Admin();

		when(manager.isAdmin(any(User.class))).thenReturn(true);
		when(boardRepository.findById(anyLong())).thenReturn(Optional.of(TestDataFactory.board()));

		UnifiedResponse<?> response = commentService.setComment(admin, commentRequest);

		verify(manager).isAdmin(any(User.class));
		verify(boardRepository).findById(anyLong());
		verify(commentRepository).save(any(Comment.class));
		TestUtil.UnifiedResponseEquals(response, 200, "댓글 작성 완료");
	}

	@Test
	void fixComment_success() {
		Comment comment = TestDataFactory.comment();

		when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

		UnifiedResponse<?> response = commentService.fixComment(saveUser, commentRequest);

		assertEquals(comment.getMessage(), commentRequest.getMessage());
		verify(commentRepository).findById(anyLong());
		TestUtil.UnifiedResponseEquals(response, 200, "댓글 수정 성공");
	}

	@Test
	void fixComment_fail() {
		User user = mock(User.class);
		when(user.getId()).thenReturn(91L);
		when(user.getEmail()).thenReturn("test");
		Comment comment = TestDataFactory.comment();

		when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

		Assertions.assertThrows(CustomException.class,
			() -> commentService.fixComment(user, commentRequest));

		verify(commentRepository).findById(anyLong());
	}
}
