package com.example.sixnumber.test.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.exception.InvalidInputException;
import com.example.sixnumber.test.entity.TestUser;
import com.example.sixnumber.test.repository.TestUserRepository;
import com.example.sixnumber.test.service.TestUserService;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/test")
public class TestUserController {

	private final TestUserService testUserService;
	private final TestUserRepository testUserRepository;

	@GetMapping("")
	public ResponseEntity<ListApiResponse<?>> getTest() {
		List<TestUser> tUser = testUserRepository.findAll();
		if (tUser.isEmpty()) {
			throw new IllegalArgumentException("유저가 없음");
		}
		ListApiResponse<TestUser> response = new ListApiResponse<>(200, "조회 성공", tUser);
		return ResponseEntity.ok(response);
	}
	@PostMapping("user")
	public ResponseEntity<ApiResponse> createUser(@RequestBody SigninRequest request) {
		return ResponseEntity.ok(testUserService.createUser(request));
	}
	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse> delete(@PathVariable Long userId) {
		return ResponseEntity.ok(testUserService.deleteUser(userId));
	}
	@PatchMapping("/{userId}")
	public ResponseEntity<ApiResponse> patch(@RequestBody OnlyMsgRequest request, @PathVariable Long userId) {
		return ResponseEntity.ok(testUserService.patchUser(userId, request));
	}
}
