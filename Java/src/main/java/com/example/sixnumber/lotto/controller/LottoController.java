package com.example.sixnumber.lotto.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthRequest;
import com.example.sixnumber.lotto.service.LottoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lotto")
public class LottoController {

	private final LottoService lottoService;

	@Cacheable(cacheNames = "MainStats", key = "'all'")
	@GetMapping("/main")
	public LottoResponse mainTopNumbers() {
		return lottoService.mainTopNumbers();
	}

	@Cacheable(value = "MonthStats", key = "#request.yearMonth")
	@GetMapping("/yearMonth")
	public LottoResponse getTopNumberForMonth(@RequestBody YearMonthRequest request) {
		return lottoService.getTopNumberForMonth(request);
	}
}
