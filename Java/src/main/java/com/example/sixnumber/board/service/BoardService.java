package com.example.sixnumber.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.sixnumber.board.dto.BoardRequest;
import com.example.sixnumber.board.dto.BoardResponse;
import com.example.sixnumber.board.dto.BoardsResponse;
import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.repository.BoardRepository;
import com.example.sixnumber.board.type.BoardStatus;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;
	private final Manager manager;

	public UnifiedResponse<?> setBoard(BoardRequest request, User user) {
		if (boardRepository.findAllByUserIdAndStatus(user.getId(), BoardStatus.UNPROCESSED).size() > 3)
			throw new CustomException(ErrorCode.BREAK_THE_ROLE);

		Board board = new Board(user, request);
		boardRepository.save(board);
		return UnifiedResponse.ok("생성 완료");
	}

	public UnifiedResponse<List<BoardsResponse>> getBoardsByStatus(Long userId, BoardStatus status) {
		List<BoardsResponse> responses = boardRepository
			.findAllByUserIdAndStatus(userId, status).stream()
			.map(BoardsResponse::new)
			.collect(Collectors.toList());

		if (responses.isEmpty()) throw new CustomException(ErrorCode.NOT_FOUND);

		return UnifiedResponse.ok("조회 성공", responses);
	}

	public UnifiedResponse<BoardResponse> getBoard(User user, Long boardId) {
		BoardResponse response = boardRepository.findById(boardId)
			.filter(b -> b.getUser().getId().equals(user.getId()))
			.map(BoardResponse::new)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		return UnifiedResponse.ok("조회 성공", response);
	}

	public UnifiedResponse<?> deleteBoard(User user, Long boardId) {
		Board board;
		if (manager.isAdmin(user)) {
			board = boardRepository.findById(boardId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		} else {
			board = boardRepository.findByIdAndUserAndCommentList_Empty(boardId, user)
				.orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));
		}

		boardRepository.delete(board);
		return UnifiedResponse.ok("삭제 성공");
	}

	public UnifiedResponse<?> fixBoard(User user, Long boardId, BoardRequest request) {
		boardRepository.findByIdAndUser(boardId, user)
			.map(board -> board.update(request))
			.orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

		return UnifiedResponse.ok("수정 성공");
	}

	public UnifiedResponse<List<BoardsResponse>> getAllBoardsByStatus(BoardStatus status) {
		List<BoardsResponse> responses = boardRepository.findAllByStatus(status).stream()
			.map(BoardsResponse::new)
			.collect(Collectors.toList());

		if (responses.isEmpty()) throw new CustomException(ErrorCode.NOT_FOUND);

		return UnifiedResponse.ok("조회 성공", responses);
	}
}
