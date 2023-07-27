package com.example.sixnumber.lotto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.lotto.entity.WinNumber;

public interface WinNumberRepository extends JpaRepository<WinNumber, Long> {
}
