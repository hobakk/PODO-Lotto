package com.example.sixnumber.lotto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthRequest;
import com.example.sixnumber.lotto.dto.YearMonthResponse;
import com.example.sixnumber.lotto.service.LottoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lotto")
public class LottoController {

	private final LottoService lottoService;

	@GetMapping("/main")
	public ResponseEntity<ItemApiResponse<LottoResponse>> mainTopNumbers() {
		return ResponseEntity.ok(ItemApiResponse.ok("조회 성공", lottoService.mainTopNumbers()));
	}

	@GetMapping("/yearMonth")
	public ResponseEntity<ItemApiResponse<LottoResponse>> getTopNumberForMonth(@RequestBody YearMonthRequest request) {
		return ResponseEntity.ok(ItemApiResponse.ok("조회 성공", lottoService.getTopNumberForMonth(request)));
	}

	@GetMapping("/yearMonth/all")
	public ResponseEntity<ItemApiResponse<YearMonthResponse>> getAllMonthStats() {
		return ResponseEntity.ok(ItemApiResponse.ok("조회 성공", lottoService.getAllMonthStats()));
	}
}
