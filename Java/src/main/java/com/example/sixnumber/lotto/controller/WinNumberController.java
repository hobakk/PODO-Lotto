package com.example.sixnumber.lotto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.lotto.dto.WinNumberResponse;
import com.example.sixnumber.lotto.service.WinNumberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/winnumber")
public class WinNumberController {
	private final WinNumberService winNumberService;

	@GetMapping("")
	public ResponseEntity<UnifiedResponse<WinNumberResponse>> getWinNumbers() {
		return ResponseEntity.ok(UnifiedResponse.ok("조회 성공", winNumberService.getWinNumbers()));
	}

	@PostMapping("/set/{round}")
	public ResponseEntity<UnifiedResponse<?>> setWinNumber(@PathVariable int round) {
		winNumberService.setWinNumbers(round);
		return ResponseEntity.ok(UnifiedResponse.ok("등록 성공"));
	}
}
