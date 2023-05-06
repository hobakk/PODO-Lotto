package com.example.sixnumber.user.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.dto.MapApiResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.StatusRequest;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/admin")
public class AdminController {

	private final AdminService adminService;

	@GetMapping("/users/{userId}")
	public ResponseEntity<ApiResponse> setAdmin(@PathVariable Long userId, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(adminService.setAdmin(user, userId));
	}

	@GetMapping("/users")
	public ResponseEntity<ListApiResponse<?>> getUsers() {
		return ResponseEntity.ok(adminService.getUsers());
	}

	@GetMapping("/chargs")
	public ResponseEntity<ListApiResponse<?>> getChargs() {
		return ResponseEntity.ok(adminService.getChargs());
	}

	@PostMapping("/users/upCash")
	public ResponseEntity<ApiResponse> upCash(@RequestBody CashRequest cashRequest) {
		return ResponseEntity.ok(adminService.upCash(cashRequest));
	}

	@PostMapping("/users/downCash")
	public ResponseEntity<ApiResponse> downCash(@RequestBody CashRequest cashRequest) {
		return ResponseEntity.ok(adminService.downCash(cashRequest));
	}

	@PostMapping("/lotto")
	public ResponseEntity<ListApiResponse<Integer>> createLotto(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(adminService.createLotto(user.getEmail()));
	}

	@PostMapping("/setStatus/{userId}")
	public ResponseEntity<?> setStatus(@PathVariable Long userId, @RequestBody StatusRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(adminService.setStatus(user, userId, request));
	}
}
