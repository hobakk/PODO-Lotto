package com.example.sixnumber.lotto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.lotto.entity.WinNumber;

public interface WinNumberRepository extends JpaRepository<WinNumber, Long> {
	Optional<WinNumber> findByTimeAndTopNumberListIn(int time, List<Integer> topNumbers);
}
