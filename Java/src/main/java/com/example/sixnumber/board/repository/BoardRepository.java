package com.example.sixnumber.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.type.BoardStatus;
import com.example.sixnumber.user.entity.User;

public interface BoardRepository extends JpaRepository<Board, Long> {
	List<Board> findAllByUserIdAndStatus(Long userId, BoardStatus status);

	Optional<Board> findByIdAndCommentEnabled(Long boardId, boolean bool);

	Optional<Board> findByIdAndUserAndCommentList_Empty(Long boardId, User user);
}
