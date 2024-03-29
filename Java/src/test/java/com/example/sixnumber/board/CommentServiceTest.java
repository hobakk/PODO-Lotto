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
	private Comment comment;
	private CommentRequest commentRequest;

	@BeforeEach
	public void setup() {
		saveUser = TestDataFactory.user();
		comment = TestDataFactory.comment();
		commentRequest = TestDataFactory.commentRequest();
	}

	@Test
	void setComment_isNotAdmin_success() {
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
	void setComment_isNotAdmin_fail() {
		User admin = TestDataFactory.Admin();

		when(manager.isAdmin(any(User.class))).thenReturn(false);
		when(boardRepository.findByIdAndCommentEnabled(anyLong(), anyBoolean()))
			.thenReturn(Optional.empty());

		Assertions.assertThrows(CustomException.class,
			() -> commentService.setComment(admin, commentRequest));

		verify(manager).isAdmin(any(User.class));
		verify(boardRepository).findByIdAndCommentEnabled(anyLong(), anyBoolean());
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
		when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

		UnifiedResponse<?> response = commentService.fixComment(saveUser, commentRequest);

		assertEquals(comment.getMessage(), commentRequest.getMessage());
		verify(commentRepository).findById(anyLong());
		verify(commentRepository).save(any(Comment.class));
		TestUtil.UnifiedResponseEquals(response, 200, "댓글 수정 성공");
	}

	@Test
	void fixComment_fail() {
		User user = mock(User.class);
		when(user.getId()).thenReturn(91L);
		when(user.getEmail()).thenReturn("test");

		when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

		Assertions.assertThrows(CustomException.class,
			() -> commentService.fixComment(user, commentRequest));

		verify(commentRepository).findById(anyLong());
	}

	@Test
	void deleteComment_success() {
		when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

		UnifiedResponse<?> response = commentService.deleteComment(saveUser, commentRequest.getId());

		assertTrue(comment.getBoard().isCommentEnabled());
		verify(commentRepository).findById(anyLong());
		verify(commentRepository).delete(any(Comment.class));
		TestUtil.UnifiedResponseEquals(response, 200, "삭제 완료");
	}

	@Test
	void deleteComment_fail() {
		User user = mock(User.class);
		when(user.getId()).thenReturn(91L);
		when(user.getEmail()).thenReturn("test");

		when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

		Assertions.assertThrows(CustomException.class,
			() -> commentService.deleteComment(user, 14L));

		verify(commentRepository).findById(anyLong());
	}
}
