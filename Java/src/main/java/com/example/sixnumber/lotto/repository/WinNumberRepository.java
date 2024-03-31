package com.example.sixnumber.lotto.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.sixnumber.lotto.entity.WinNumber;

public interface WinNumberRepository extends JpaRepository<WinNumber, Long> {
	Boolean existsWinNumberByTime(int time);

	@Query("SELECT w FROM WinNumber w ORDER BY w.time DESC")
	List<WinNumber> findTopByTime(Pageable pageable);

	Optional<WinNumber> findByTime(int time);
}
