package com.example.sixnumber.board.service;

import org.springframework.stereotype.Service;

import com.example.sixnumber.board.dto.CommentRequest;
import com.example.sixnumber.board.entity.Comment;
import com.example.sixnumber.board.repository.BoardRepository;
import com.example.sixnumber.board.repository.CommentRepository;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;

	public UnifiedResponse<?> setComment(User user, CommentRequest request) {
		Comment comment = boardRepository.findById(request.getBoardId())
			.filter(board -> board.getUser().equals(user) || user.getRole().equals(UserRole.ROLE_ADMIN))
			.map(board ->  new Comment(user, board, request.getMessage()))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		commentRepository.save(comment);
		return UnifiedResponse.ok("댓글 작성 완료");
	}
}
