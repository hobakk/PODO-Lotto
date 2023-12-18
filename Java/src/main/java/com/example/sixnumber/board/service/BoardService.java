package com.example.sixnumber.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.sixnumber.board.dto.BoardRequest;
import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.repository.BoardRepository;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

	public UnifiedResponse<?> setBoard(BoardRequest request, User user) {
		Board board = new Board(user, request);
		boardRepository.save(board);
		return UnifiedResponse.ok("생성 완료");
	}

	public UnifiedResponse<List<Board>> getBoards(Long userId) {
		List<Board> boardList = userRepository.findById(userId)
			.map(user -> user.getBoardList().stream()
				.map(Board::getResult)
				.collect(Collectors.toList()))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		return UnifiedResponse.ok("조회 성공", boardList);
	}
}
