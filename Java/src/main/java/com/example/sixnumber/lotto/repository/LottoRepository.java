package com.example.sixnumber.lotto.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sixnumber.lotto.entity.Lotto;

public interface LottoRepository extends JpaRepository<Lotto, Long> {

	@Query(value = "SELECT l FROM Lotto l WHERE l.creationDate = :yearMonth")
	Optional<Lotto> findByTopNumbersForMonth(@Param("yearMonth") YearMonth yearMonth);

	@Query(value = "SELECT l FROM Lotto l WHERE l.subject = 'main'")
	Optional<Lotto> findByMain();

	@Query(value = "SELECT l FROM Lotto l WHERE l.subject LIKE '%Stats%'")
	List<Lotto> findAllByMonthStats();
}
