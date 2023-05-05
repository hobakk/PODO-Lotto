package com.example.sixnumber.lotto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.sixnumber.lotto.entity.SixNumber;

public interface SixNumberRepository extends JpaRepository<SixNumber, Long> {
	// @Modifying
	// @Query(value = "SELECT ")
}
