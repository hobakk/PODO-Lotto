package com.example.sixnumber.board.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.sixnumber.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne()
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "board_id")
	private Board board;

	@Column(nullable = false)
	private String message;
	private LocalDate correctionDate;

	public Comment(User user, Board board, String message) {
		this.user = user;
		this.board = board;
		this.message = message;
		this.correctionDate = null;
	}

	public Comment update(String newMsg) {
		this.message = newMsg;
		this.correctionDate = LocalDate.now();
		return this;
	}

	public void deleteFromBoard() {
		this.board.getCommentList().remove(this);
	}
}
