package com.example.sixnumber.lotto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.lotto.service.WinNumberService;
import com.example.sixnumber.user.dto.WinNumberRequest;
import com.example.sixnumber.user.dto.WinNumberResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/winnumber")
public class WinNumberController {
	private final WinNumberService winNumberService;

	@GetMapping("/")
	public ResponseEntity<ItemApiResponse<WinNumberResponse>> getWinNumbers() {
		return ResponseEntity.ok(ItemApiResponse.ok("조회 성공", winNumberService.getWinNumbers()));
	}

	@PostMapping("/")
	public ResponseEntity<ApiResponse> setWinNumber(WinNumberRequest request) {
		winNumberService.setWinNumbers(request);
		return ResponseEntity.ok(ApiResponse.ok("등록 성공"));
	}
}
