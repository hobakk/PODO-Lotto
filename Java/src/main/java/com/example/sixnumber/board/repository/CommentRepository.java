package com.example.sixnumber.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.board.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
