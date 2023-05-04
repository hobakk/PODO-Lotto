package com.example.sixnumber.user.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.DataApiResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/admin")
public class AdminController {

	private final AdminService adminService;

	@GetMapping("/users")
	public ResponseEntity<DataApiResponse<?>> getUsers(HttpServletRequest request) {
		return ResponseEntity.ok(adminService.getUsers(request));
	}

	@GetMapping("/chargs")
	public ResponseEntity<DataApiResponse<?>> getChargs(HttpServletRequest request) {
		return ResponseEntity.ok(adminService.getChargs(request));
	}

	@PostMapping("/upCash")
	public ResponseEntity<ApiResponse> upCash(@RequestBody CashRequest cashRequest, HttpServletRequest httpServletRequest) {
		return ResponseEntity.ok(adminService.upCash(cashRequest, httpServletRequest));
	}

	@PostMapping("/downCash/{userId}")
	public ResponseEntity<ApiResponse> downCash(@RequestBody CashRequest cashRequest, HttpServletRequest httpServletRequest) {
		return ResponseEntity.ok(adminService.downCash(cashRequest, httpServletRequest));
	}
}
