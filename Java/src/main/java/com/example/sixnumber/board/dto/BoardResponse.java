package com.example.sixnumber.board.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.entity.Comment;
import com.example.sixnumber.board.type.BoardStatus;

import lombok.Getter;

@Getter
public class BoardResponse {
	private final Long boardId;
	private final String userName;
	private final String subject;
	private final String contents;
	private final BoardStatus status;
	private final List<Comment> commentList;
	private final LocalDate correctionDate;

	public BoardResponse(Board board) {
		this.boardId = board.getId();
		this.userName = board.getUser().getNickname();
		this.subject = board.getSubject();
		this.contents = board.getContents();
		this.status = board.getStatus();
		this.commentList = board.getCommentList();
		this.correctionDate = board.getCorrectionDate();
	}
}
