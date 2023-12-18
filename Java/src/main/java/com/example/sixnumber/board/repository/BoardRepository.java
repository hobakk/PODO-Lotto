package com.example.sixnumber.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
