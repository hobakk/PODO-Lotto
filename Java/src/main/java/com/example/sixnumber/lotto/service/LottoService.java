package com.example.sixnumber.lotto.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthResponse;
import com.example.sixnumber.lotto.repository.LottoRepository;

import lombok.AllArgsConstructor;

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
				Map<String, Integer> map = new HashMap<>();
				for (int i = 0; i < lotto.getCountList().size(); i++) {
					map.put(Integer.toString(i + 1), lotto.getCountList().get(i));
				}

				String result = manager.getTopNumbersAsString(map);
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
		List<String> yearMonthList = new ArrayList<>();
		lottoRepository.findAllByMonthStats().stream()
			.findAny()
			.map(lotto -> yearMonthList.add((lotto.getCreationDate()).toString()))
			.orElseThrow(() -> new IllegalArgumentException("해당 정보를 찾을 수 없습니다"));

		return new YearMonthResponse(yearMonthList);
	}
}
