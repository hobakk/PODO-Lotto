package com.example.sixnumber.lotto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sixnumber.lotto.entity.Lotto;

public interface LottoRepository extends JpaRepository<Lotto, Long> {

}
