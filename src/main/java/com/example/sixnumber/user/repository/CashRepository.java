package com.example.sixnumber.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.user.entity.Cash;

public interface CashRepository extends JpaRepository<Cash, Long> {
	List<Cash> findByUserId(Long userId);
}
