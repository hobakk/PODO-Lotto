package com.example.sixnumber.board.dto;

import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.type.BoardStatus;

import lombok.Getter;

@Getter
public class BoardResponse {
	private final Long boardId;
	private final String userName;
	private final String subject;
	private final String contents;
	private final BoardStatus status;

	public BoardResponse(Board board) {
		this.boardId = board.getId();
		this.userName = board.getUser().getNickname();
		this.subject = board.getSubject();
		this.contents = board.getContents();
		this.status = board.getStatus();
	}
}
