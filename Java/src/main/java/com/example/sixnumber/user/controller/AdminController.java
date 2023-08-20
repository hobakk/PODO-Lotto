package com.example.sixnumber.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.user.dto.AdminGetChargingResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.UsersResponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/admin")
public class AdminController {

	private final AdminService adminService;

	@GetMapping("/users")
	public ResponseEntity<UnifiedResponse<List<UsersResponse>>> getUsers() {
		return ResponseEntity.ok(adminService.getUsers());
	}

	@GetMapping("/charges")
	public ResponseEntity<UnifiedResponse<List<AdminGetChargingResponse>>> getCharges() {
		return ResponseEntity.ok(adminService.getCharges());
	}

	@GetMapping("/search")
	public ResponseEntity<UnifiedResponse<AdminGetChargingResponse>> searchCharging(
		@RequestParam("msg") String msg,
		@RequestParam("cash") int cash)
	{
		return ResponseEntity.ok(adminService.searchCharging(msg, cash));
	}

	@PatchMapping("/users/{userId}")
	public ResponseEntity<UnifiedResponse<?>> setAdmin(
		@PathVariable Long userId,
		@RequestBody OnlyMsgRequest request,
		@AuthenticationPrincipal User user)
	{
		return ResponseEntity.ok(adminService.setAdmin(request, user, userId));
	}

	@PatchMapping("/users/up-cash")
	public ResponseEntity<UnifiedResponse<?>> upCash(@RequestBody CashRequest cashRequest) {
		return ResponseEntity.ok(adminService.upCash(cashRequest));
	}

	@PatchMapping("/users/down-cash")
	public ResponseEntity<UnifiedResponse<?>> downCash(@RequestBody CashRequest cashRequest) {
		return ResponseEntity.ok(adminService.downCash(cashRequest));
	}

	@PostMapping("/lotto")
	public ResponseEntity<UnifiedResponse<?>> createLotto(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(adminService.createLotto(user.getEmail()));
	}

	@PatchMapping("/status/{userId}")
	public ResponseEntity<UnifiedResponse<?>> setStatus(@PathVariable Long userId, @RequestBody OnlyMsgRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(adminService.setStatus(user, userId, request));
	}

	@PatchMapping("/role/{userId}")
	public ResponseEntity<UnifiedResponse<?>> setRole(@PathVariable Long userId, @RequestBody OnlyMsgRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(adminService.setRole(user, userId, request));
	}
}
