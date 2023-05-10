package com.example.sixnumber.user.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ReleasePaidRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.WithdrawRequest;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse> signup(@RequestBody SignupRequest request) {
		return ResponseEntity.ok(userService.signUp(request));
	}

	@PostMapping("/signin")
	public ResponseEntity<ApiResponse> signin(@RequestBody SigninRequest request, HttpServletResponse response) {
		String generatedToken = userService.signIn(request);
		response.addHeader(JwtProvider.AUTHORIZATION_HEADER, generatedToken);
		return ResponseEntity.ok(ApiResponse.ok("로그인 성공"));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@AuthenticationPrincipal User user, HttpServletResponse response) {
		response.setHeader(JwtProvider.AUTHORIZATION_HEADER, "");
		userService.logout(user);
		return ResponseEntity.ok("로그아웃 완료");
	}

	@PostMapping("/withdraw")
	public ResponseEntity<?> withdraw(@RequestBody WithdrawRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.withdraw(request, user.getEmail()));
	}

	// 로그인 후 화면에 바로 띄울지에 대한 고민
	@GetMapping("/cash")
	public ResponseEntity<ApiResponse> getCash(@AuthenticationPrincipal User user) {
		int cash = userService.getCash(user);
		return ResponseEntity.ok().body(ApiResponse.ok("조회 성공\n" + cash));
	}

	@PostMapping("/cash")
	public ResponseEntity<ApiResponse> charging(@RequestBody ChargingRequest chargingRequest, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.charging(chargingRequest, user.getId()));
	}

	@PostMapping("/setPaid")
	public ResponseEntity<?> setPaid(@RequestBody ReleasePaidRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.setPaid(request ,user.getEmail()));
	}
}
