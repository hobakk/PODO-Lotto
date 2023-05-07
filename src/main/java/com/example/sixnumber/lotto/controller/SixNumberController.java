package com.example.sixnumber.lotto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.service.SixNumberService;
import com.example.sixnumber.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/sixnum")
public class SixNumberController {

	private final SixNumberService sixNumberService;

	@PostMapping("/main")
	public ResponseEntity<?> mainTopNumbers(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(sixNumberService.mainTopNumbers(user));
	}

	@PostMapping("")
	public ResponseEntity<?> buyNumbers(@RequestBody BuyNumberRequest buyNumberRequest, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(sixNumberService.buyNumber(buyNumberRequest, user));
	}

	@PostMapping("/Repetition")
	public ResponseEntity<?> buyRepetitionTopNumbers(@RequestBody BuyNumberRequest buyNumberRequest, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(sixNumberService.buyRepetitionNumber(buyNumberRequest, user));
	}
}
