package com.example.sixnumber.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.board.entity.Board;
import com.example.sixnumber.board.type.BoardStatus;

public interface BoardRepository extends JpaRepository<Board, Long> {
	List<Board> findAllByUserIdAndStatus(Long userId, BoardStatus status);
}