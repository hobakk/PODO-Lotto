package com.example.sixnumber.lotto.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthResponse;
import com.example.sixnumber.lotto.entity.Lotto;
import com.example.sixnumber.lotto.repository.LottoRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class LottoService {

	private final LottoRepository lottoRepository;
	private final Manager manager;

	@Cacheable(cacheNames = "MainStats", key = "'all'")
	public LottoResponse mainTopNumbers() {
		return lottoRepository.findByMain()
			.map(lotto -> {
				String result = manager.revisedTopIndicesAsStr(lotto.getCountList());
				return new LottoResponse(lotto.getCountList(), result);
			})
			.orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));
	}

	@Cacheable(value = "MonthStats", key = "#yearMonth")
	public LottoResponse getTopNumberForMonth(YearMonth yearMonth) {
		return lottoRepository.findByTopNumbersForMonth(yearMonth)
			.map(lotto -> new LottoResponse(lotto.getCountList(), lotto.getTopNumber()))
			.orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));
	}

	@Cacheable(value = "MonthStats", key = "'all'")
	public YearMonthResponse getAllMonthStats() {
		List<Lotto> lottoList = lottoRepository.findAllByMonthStats();
		if (lottoList.isEmpty()) throw new IllegalArgumentException("해당 정보를 찾을 수 없습니다");

		List<String> yearMonthList = new ArrayList<>();
		for (Lotto lotto : lottoList) {
			yearMonthList.add((lotto.getCreationDate()).toString());
		}

		return new YearMonthResponse(yearMonthList);
	}
}
