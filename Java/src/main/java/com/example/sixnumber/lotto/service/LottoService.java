package com.example.sixnumber.lotto.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthRequest;
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
		Lotto lotto = lottoRepository.findByMain().orElseThrow(
			() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));

		List<Integer> countList = new ArrayList<>(lotto.getCountList());
		String result = manager.revisedTopIndicesAsStr(countList);
		return new LottoResponse(countList, result);
	}

	@Cacheable(value = "MonthStats", key = "#request.yearMonth")
	public LottoResponse getTopNumberForMonth(YearMonthRequest request) {
		Lotto lotto = lottoRepository.findByTopNumbersForMonth(request.getYearMonth())
			.orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));

		List<Integer> countList = new ArrayList<>(lotto.getCountList());
		String result = lotto.getTopNumber();
		return new LottoResponse(countList, result);
	}
}
