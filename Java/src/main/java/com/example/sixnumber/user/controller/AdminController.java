package com.example.sixnumber.user.controller;

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

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.user.dto.AdminGetChargingResponse;
import com.example.sixnumber.user.dto.CashRequest;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.UsersReponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/admin")
public class AdminController {

	private final AdminService adminService;

	@GetMapping("/users")
	public ResponseEntity<ListApiResponse<UsersReponse>> getUsers() {
		return ResponseEntity.ok(adminService.getUsers());
	}

	@GetMapping("/charges")
	public ResponseEntity<ListApiResponse<AdminGetChargingResponse>> getCharges() {
		return ResponseEntity.ok(adminService.getCharges());
	}

	@GetMapping("/search")
	public ResponseEntity<ItemApiResponse<AdminGetChargingResponse>> searchCharging(@RequestParam("msg") String msg, @RequestParam("cash") int cash) {
		return ResponseEntity.ok(adminService.searchCharging(msg, cash));
	}

	@PatchMapping("/users/{userId}")
	public ResponseEntity<ApiResponse> setAdmin(@PathVariable Long userId, @RequestBody OnlyMsgRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(adminService.setAdmin(request, user, userId));
	}

	@PatchMapping("/users/up-cash")
	public ResponseEntity<ApiResponse> upCash(@RequestBody CashRequest cashRequest) {
		return ResponseEntity.ok(adminService.upCash(cashRequest));
	}

	@PatchMapping("/users/down-cash")
	public ResponseEntity<ApiResponse> downCash(@RequestBody CashRequest cashRequest) {
		return ResponseEntity.ok(adminService.downCash(cashRequest));
	}

	@PostMapping("/lotto")
	public ResponseEntity<ApiResponse> createLotto(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(adminService.createLotto(user.getEmail()));
	}

	@PatchMapping("/status/{userId}")
	public ResponseEntity<ApiResponse> setStatus(@PathVariable Long userId, @RequestBody OnlyMsgRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(adminService.setStatus(user, userId, request));
	}

	@PatchMapping("/role/{userId}")
	public ResponseEntity<ApiResponse> setRole(@PathVariable Long userId, @RequestBody OnlyMsgRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(adminService.setRole(user, userId, request));
	}
}
