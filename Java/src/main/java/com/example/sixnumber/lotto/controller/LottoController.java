package com.example.sixnumber.lotto.controller;

import java.time.YearMonth;

import com.example.sixnumber.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.lotto.dto.LottoResponse;
import com.example.sixnumber.lotto.dto.YearMonthResponse;
import com.example.sixnumber.lotto.service.LottoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lotto")
public class LottoController {

	private final LottoService lottoService;

	@PostMapping("/main/admin")
	public ResponseEntity<UnifiedResponse<?>> createLotto() {
		return ResponseEntity.ok(lottoService.createLotto());
	}

	@GetMapping("/main/admin")
	public ResponseEntity<Boolean> checkMain() {
		return ResponseEntity.ok(lottoService.checkMain());
	}

	@GetMapping("/main")
	public ResponseEntity<UnifiedResponse<LottoResponse>> mainTopNumbers() {
		return ResponseEntity.ok(UnifiedResponse.ok("조회 성공", lottoService.mainTopNumbers()));
	}

	@GetMapping("/yearMonth")
	public ResponseEntity<UnifiedResponse<LottoResponse>> getMonthlyStats(@RequestParam YearMonth yearMonth) {
		return ResponseEntity.ok(UnifiedResponse.ok("조회 성공", lottoService.getMonthlyStats(yearMonth)));
	}

	@GetMapping("/yearMonth/all")
	public ResponseEntity<UnifiedResponse<YearMonthResponse>> getAllMonthlyStats() {
		return ResponseEntity.ok(UnifiedResponse.ok("조회 성공", lottoService.getAllMonthlyStats()));
	}

	@PostMapping("/stats/{year}/{month}")
	public ResponseEntity<UnifiedResponse<?>> createMonthlyReport(
		@PathVariable int year,
		@PathVariable int month
	) {
		return ResponseEntity.ok(lottoService.createMonthlyReport(year, month));
	}

	@PostMapping("/stats/{year}")
	public ResponseEntity<UnifiedResponse<?>> createYearlyReport(@PathVariable int year) {
		return ResponseEntity.ok(lottoService.createYearlyReport(year));
	}

	@GetMapping("/yearly/all")
	public ResponseEntity<UnifiedResponse<YearMonthResponse>> getAllYearlyStatsIndex() {
		return ResponseEntity.ok(UnifiedResponse.ok(
				"모든 연도별 통계 인덱스 조회성공",
				lottoService.getAllYearlyStatsIndex())
		);
	}

	@GetMapping("/yearly/{year}")
	public ResponseEntity<UnifiedResponse<LottoResponse>> getYearlyStats(@PathVariable int year) {
		return ResponseEntity.ok(UnifiedResponse.ok(
						year + "년 통계 조회성공",
						lottoService.getYearlyStats(year))
		);
	}
}
