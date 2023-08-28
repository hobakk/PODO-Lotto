package com.example.sixnumber.lotto.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.dto.StatisticalNumberRequest;
import com.example.sixnumber.lotto.service.SixNumberService;
import com.example.sixnumber.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/sixnum")
public class SixNumberController {

	private final SixNumberService sixNumberService;

	@PostMapping("")
	public ResponseEntity<UnifiedResponse<List<String>>> buyNumbers(
		@RequestBody BuyNumberRequest buyNumberRequest,
		@AuthenticationPrincipal User user)
	{
		return ResponseEntity.ok(sixNumberService.buyNumber(buyNumberRequest, user));
	}

	@PostMapping("/repetition")
	public ResponseEntity<UnifiedResponse<List<String>>> statisticalNumber(
		@RequestBody StatisticalNumberRequest BuyRepetitionNumberRequest,
		@AuthenticationPrincipal User user)
	{
		return ResponseEntity.ok(sixNumberService.statisticalNumber(BuyRepetitionNumberRequest, user));
	}

	@GetMapping("/recent")
	public ResponseEntity<UnifiedResponse<List<String>>> getRecentBuyNumbers(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(sixNumberService.getRecentBuyNumbers(user));
	}
}
