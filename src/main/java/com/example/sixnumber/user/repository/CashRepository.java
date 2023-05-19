package com.example.sixnumber.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.sixnumber.user.entity.Cash;

public interface CashRepository extends JpaRepository<Cash, Long> {
	@Modifying
	@Query(value = "SELECT c FROM Cash c WHERE c.processing = 'BEFORE'")
	List<Cash> processingEqaulBefore();

	@Modifying
	@Query(value = "SELECT c FROM Cash c WHERE c.processing = 'AFTER'")
	List<Cash> processingEqaulAfter();

	List<Cash> findAllByUserId(Long userId);
}
