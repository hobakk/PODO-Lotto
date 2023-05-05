package com.example.sixnumber.lotto.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.lotto.dto.BuyNumberRequest;
import com.example.sixnumber.lotto.service.SixNumberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/sixnum")
public class SixNumberController {

	private final SixNumberService sixNumberService;

	@PostMapping("/buy")
	public ResponseEntity<?> buyNumber(@RequestBody BuyNumberRequest buyNumberRequest, HttpServletRequest httpServletRequest) {
		return ResponseEntity.ok(sixNumberService.buyNumber(buyNumberRequest, httpServletRequest));
	}
}
