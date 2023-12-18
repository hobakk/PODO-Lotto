package com.example.sixnumber.board.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.sixnumber.board.dto.BoardRequest;
import com.example.sixnumber.board.type.BoardStatus;
import com.example.sixnumber.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "board")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	@Column(nullable = false)
	private String subject;
	@Column(nullable = false)
	private String contents;
	@Enumerated(EnumType.STRING)
	private BoardStatus status;

	public Board(User user, BoardRequest request) {
		this.user = user;
		this.subject = request.getSubject();
		this.contents = request.getContents();
		this.status = BoardStatus.UNPROCESSED;
	}
}





