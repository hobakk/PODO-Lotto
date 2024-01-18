package com.example.sixnumber.board.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.board.dto.BoardRequest;
import com.example.sixnumber.board.dto.BoardResponse;
import com.example.sixnumber.board.dto.BoardsResponse;
import com.example.sixnumber.board.service.BoardService;
import com.example.sixnumber.board.type.BoardStatus;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
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
	public ResponseEntity<UnifiedResponse<List<BoardsResponse>>> getBoardsByStatus(
		@RequestParam BoardStatus status,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(boardService.getBoardsByStatus(user.getId(), status));
	}

	@GetMapping("/{boardId}")
	public ResponseEntity<UnifiedResponse<BoardResponse>> getBoard(
		@PathVariable Long boardId,
		@AuthenticationPrincipal User user
	) {
		return	ResponseEntity.ok(boardService.getBoard(user, boardId));
	}

	@DeleteMapping("/{boardId}")
	public ResponseEntity<UnifiedResponse<?>> deleteBoard(
		@PathVariable Long boardId,
		@AuthenticationPrincipal User user
	) {
		return	ResponseEntity.ok(boardService.deleteBoard(user, boardId));
	}

	@PatchMapping("/{boardId}")
	public ResponseEntity<UnifiedResponse<?>> fixBoard(
		@PathVariable Long boardId,
		@RequestBody BoardRequest request,
		@AuthenticationPrincipal User user
	) {
		return	ResponseEntity.ok(boardService.updateBoard(user, boardId, request));
	}

	@GetMapping("/admin")
	public ResponseEntity<UnifiedResponse<List<BoardsResponse>>> getAllBoardsByStatus(
		@RequestParam BoardStatus status
	) {
		return ResponseEntity.ok(boardService.getAllBoardsByStatus(status));
	}
}
