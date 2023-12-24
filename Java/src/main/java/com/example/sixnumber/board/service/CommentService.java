package com.example.sixnumber.board.service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.sixnumber.board.dto.CommentRequest;
import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.entity.Comment;
import com.example.sixnumber.board.repository.BoardRepository;
import com.example.sixnumber.board.repository.CommentRepository;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;
	private final Manager manager;

	public UnifiedResponse<?> setComment(User user, CommentRequest request) {
		Comment comment;
		if (manager.isAdmin(user)) {
			comment = boardRepository.findById(request.getId())
				.map(board -> {
					board.setComment();
					return new Comment(user, board, request.getMessage());
				})
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		} else {
			comment = boardRepository.findByIdAndCommentEnabled(request.getId(), true)
				.map(board ->  {
					board.setCommentWithAdmin();
					return new Comment(user, board, request.getMessage());
				})
				.orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));
		}

		commentRepository.save(comment);
		return UnifiedResponse.ok("댓글 작성 완료");
	}

	public UnifiedResponse<?> fixComment(User user, CommentRequest request) {
		Comment updateComment = commentRepository.findById(request.getId())
			.filter(comment -> comment.getUser().equals(user) || user.getRole().equals(UserRole.ROLE_ADMIN))
			.map(comment -> comment.update(request.getMessage()))
			.orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

		commentRepository.save(updateComment);
		return UnifiedResponse.ok("댓글 수정 성공");
	}

	@Transactional
	public UnifiedResponse<?> deleteComment(User user, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.filter(c -> c.getUser().equals(user) || user.getRole().equals(UserRole.ROLE_ADMIN))
			.orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

		Board board = comment.getBoard();
		board.setCommentEnabled();
		int target = 0;
		for (int i = 0; i < board.getCommentList().size(); i++) {
			if (board.getCommentList().get(i).getId().equals(commentId)) {
				target = i;
				break;
			}
		}

		board.getCommentList().remove(target);
		boardRepository.save(board);
		commentRepository.delete(comment);
		return UnifiedResponse.ok("삭제 완료");
	}
}
