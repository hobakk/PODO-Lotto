package com.example.sixnumber.board.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.board.dto.CommentRequest;
import com.example.sixnumber.board.service.CommentService;
import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("")
	public ResponseEntity<UnifiedResponse<?>> setComment(
		@RequestBody CommentRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(commentService.setComment(user, request));
	}

	@PatchMapping("")
	public ResponseEntity<UnifiedResponse<?>> fixComment(
		@RequestBody CommentRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(commentService.fixComment(user, request));
	}

	@DeleteMapping("")
	public ResponseEntity<UnifiedResponse<?>> deleteComment(
		@RequestBody CommentRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(commentService.deleteComment(user, request));
	}
}
