package com.example.sixnumber.board.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.board.dto.BoardRequest;
import com.example.sixnumber.board.dto.BoardResponse;
import com.example.sixnumber.board.service.BoardService;
import com.example.sixnumber.board.type.BoardStatus;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

	private final BoardService boardService;

	@PostMapping("")
	public ResponseEntity<UnifiedResponse<?>> setBoard(
		@RequestBody BoardRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(boardService.setBoard(request, user));
	}

	@GetMapping("")
	public ResponseEntity<UnifiedResponse<List<BoardResponse>>> getBoards(
		@RequestParam BoardStatus status,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(boardService.getBoards(user.getId(), status));
	}

	@GetMapping("/{boardId}")
	public ResponseEntity<UnifiedResponse<BoardResponse>> getBoard(
		@PathVariable Long boardId,
		@AuthenticationPrincipal User user
	) {
		return	ResponseEntity.ok(boardService.getBoard(user, boardId));
	}
}
