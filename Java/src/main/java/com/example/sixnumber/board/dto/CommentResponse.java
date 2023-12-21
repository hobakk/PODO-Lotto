package com.example.sixnumber.board.dto;

import com.example.sixnumber.board.entity.Comment;

import lombok.Getter;

@Getter
public class CommentResponse {
	private final String nickname;
	private final String message;

	public CommentResponse(Comment comment) {
		this.nickname = comment.getUser().getNickname();
		this.message = comment.getMessage();
	}
}
