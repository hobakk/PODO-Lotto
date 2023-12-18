package com.example.sixnumber.board.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

	@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> commentList;

	public Board(User user, BoardRequest request) {
		this.user = user;
		this.subject = request.getSubject();
		this.contents = request.getContents();
		this.status = BoardStatus.UNPROCESSED;
		this.commentList = new ArrayList<>();
	}

	public Board(String subject, String contents) {
		this.id = this.getId();
		this.user = this.getUser();
		this.subject = subject;
		this.contents = contents;
		this.status = this.getStatus();
		this.commentList = null;
	}

	public Board getResult() {
		return new Board(setValue(this.subject), setValue(this.contents));
	}

	public String setValue(String target) {
		return target.length() > 13 ? target.substring(10) + "..." : target;
	}
}





