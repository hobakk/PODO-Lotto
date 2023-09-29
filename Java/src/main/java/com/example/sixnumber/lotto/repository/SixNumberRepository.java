package com.example.sixnumber.lotto.repository;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sixnumber.lotto.entity.SixNumber;
import com.example.sixnumber.user.entity.User;

public interface SixNumberRepository extends JpaRepository<SixNumber, Long> {
	@Query(value = "SELECT s FROM SixNumber s WHERE s.user = :user ORDER BY s.buyDate DESC")
	List<SixNumber> findByRecentBuyNumbers(@Param("user") User user, Pageable pageable);

	@Query(value = "SELECT s FROM SixNumber s WHERE YEARMONTH (s.buyDate) = :yearMonth")
	List<SixNumber> findAllByBuyDate(@Param("yearMonth") YearMonth yearMonth);
}
