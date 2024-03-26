package com.example.sixnumber.lotto.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

	@PostMapping("/{total}")
	public ResponseEntity<UnifiedResponse<List<String>>> buyNumbers(@PathVariable int total) {
		return ResponseEntity.ok(sixNumberService.buyNumber(total));
	}

	@PostMapping("/repetition")
	public ResponseEntity<UnifiedResponse<List<String>>> statisticalNumber(
		@RequestBody StatisticalNumberRequest BuyRepetitionNumberRequest,
		@AuthenticationPrincipal User user)
	{
		return ResponseEntity.ok(sixNumberService.statisticalNumber(BuyRepetitionNumberRequest, user));
	}

	@GetMapping("/recent")
	public ResponseEntity<UnifiedResponse<List<String>>> getRecentBuyNumbers(
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(sixNumberService.getRecentBuyNumbers(user));
	}
}
