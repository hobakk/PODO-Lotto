package com.example.sixnumber.user.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sixnumber.user.entity.Statement;

public interface StatementRepository extends JpaRepository<Statement, Long> {
	@Query("SELECT s FROM Statement s WHERE s.id = :statementId AND s.localDate >= :lastMonth")
	Optional<Statement> findByIdAndAfterLastMonth(
		@Param("statementId") Long statementId,
		@Param("lastMonth") LocalDate lastMonth
	);
}
