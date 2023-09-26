package com.example.sixnumber.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.user.entity.Statement;

public interface StatementRepository extends JpaRepository<Statement, Long> {
}
