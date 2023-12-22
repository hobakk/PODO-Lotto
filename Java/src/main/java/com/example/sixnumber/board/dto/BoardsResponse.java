package com.example.sixnumber.board.dto;

import com.example.sixnumber.board.entity.Board;

import lombok.Getter;

@Getter
public class BoardsResponse {
	private final Long boardId;
	private final String subject;
	private final String contents;

	public BoardsResponse(Board board) {
		this.boardId = board.getId();
		this.subject = setValue(board.getSubject());
		this.contents = setValue(board.getContents());
	}

	public String setValue(String word) {
		return word.length() > 13 ? word.substring(10) + "..." : word;
	}
}
