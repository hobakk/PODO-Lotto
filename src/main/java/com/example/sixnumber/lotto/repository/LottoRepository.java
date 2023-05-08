package com.example.sixnumber.lotto.repository;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sixnumber.lotto.entity.Lotto;

public interface LottoRepository extends JpaRepository<Lotto, Long> {

	@Query(value = "SELECT l FROM Lotto l WHERE l.creationDate = :yearMonth")
	List<Lotto> findByTopNubersForMonth(@Param("yearMonth") YearMonth yearMonth);
}
