package com.example.sixnumber.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.user.entity.Cash;

public interface CashRepository extends JpaRepository<Cash, Long> {
}
