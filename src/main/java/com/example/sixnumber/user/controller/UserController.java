package com.example.sixnumber.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
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
		HttpHeaders headers = new HttpHeaders();
		response.addHeader(JwtProvider.AUTHORIZATION_HEADER, generatedToken);
		return ResponseEntity.ok().headers(headers).body(ApiResponse.ok("로그인 성공"));
	}

	@GetMapping("/cash")
	public ResponseEntity<ApiResponse> getCash(HttpServletRequest request) {
		int cash = userService.getCash(request);
		return ResponseEntity.ok().body(ApiResponse.ok("조회 성공\n" + cash));
	}

	@PostMapping("/cash")
	public ResponseEntity<ApiResponse> charging(@RequestBody ChargingRequest chargingRequest, HttpServletRequest httpServletRequest) {
		return ResponseEntity.ok(userService.charging(chargingRequest, httpServletRequest));
	}
}
