package com.example.sixnumber.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.sixnumber.board.dto.BoardRequest;
import com.example.sixnumber.board.dto.BoardResponse;
import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.repository.BoardRepository;
import com.example.sixnumber.board.type.BoardStatus;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;

	public UnifiedResponse<?> setBoard(BoardRequest request, User user) {
		if (boardRepository.findAllByUserIdAndStatus(user.getId(), BoardStatus.UNPROCESSED).size() > 3)
			throw new CustomException(ErrorCode.BREAK_THE_ROLE);

		Board board = new Board(user, request);
		boardRepository.save(board);
		return UnifiedResponse.ok("생성 완료");
	}

	public UnifiedResponse<List<BoardResponse>> getBoards(Long userId, BoardStatus status) {
		List<BoardResponse> responses = boardRepository
			.findAllByUserIdAndStatus(userId, status).stream()
			.map(board -> new BoardResponse(board.getResult()))
			.collect(Collectors.toList());

		return UnifiedResponse.ok("조회 성공", responses);
	}
}
