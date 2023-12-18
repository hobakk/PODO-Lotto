package com.example.sixnumber.board.service;

import org.springframework.stereotype.Service;

import com.example.sixnumber.board.dto.BoardRequest;
import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.repository.BoardRepository;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;

	public UnifiedResponse<?> setBoard(BoardRequest request, User user) {
		Board board = new Board(user, request);
		boardRepository.save(board);
		return UnifiedResponse.ok("생성 완료");
	}
}
