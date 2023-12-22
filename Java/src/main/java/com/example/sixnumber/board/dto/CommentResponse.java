package com.example.sixnumber.board.dto;

import com.example.sixnumber.board.entity.Comment;

import lombok.Getter;

@Getter
public class CommentResponse {
	private final Long commentId;
	private final String nickname;
	private final String message;

	public CommentResponse(Comment comment) {
		this.commentId = comment.getId();
		this.nickname = comment.getUser().getNickname();
		this.message = comment.getMessage();
	}
}
