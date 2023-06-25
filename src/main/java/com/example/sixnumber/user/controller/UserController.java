package com.example.sixnumber.user.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ListApiResponse;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ChargingResponse;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.StatementResponse;
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
		Cookie jwtCookie = new Cookie("JWT", userService.signIn(request));
		jwtCookie.setPath("/");
		jwtCookie.setHttpOnly(true);
		jwtCookie.setMaxAge(1800);
		response.addCookie(jwtCookie);
		return ResponseEntity.ok(ApiResponse.ok("로그인 성공"));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse> logout(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.logout(user));
	}

	@PatchMapping("/withdraw")
	public ResponseEntity<ApiResponse> withdraw(@RequestBody OnlyMsgRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.withdraw(request, user.getEmail()));
	}

	// 로그인 후 화면에 바로 띄울지에 대한 고민
	@GetMapping("/cash")
	public ResponseEntity<ApiResponse> getCash(@AuthenticationPrincipal User user) {
		int cash = userService.getCash(user);
		return ResponseEntity.ok().body(ApiResponse.ok("조회 성공" + cash));
	}

	@GetMapping("/charging")
	public ResponseEntity<ListApiResponse<ChargingResponse>> getChargings(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.getCharges(user.getId()));
	}

	@PostMapping("/charging")
	public ResponseEntity<ApiResponse> charging(@RequestBody ChargingRequest chargingRequest, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.charging(chargingRequest, user));
	}

	@PostMapping("/paid")
	public ResponseEntity<ApiResponse> setPaid(@RequestBody OnlyMsgRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.setPaid(request ,user.getEmail()));
	}

	@GetMapping("/statement")
	public ResponseEntity<ListApiResponse<StatementResponse>> getStatement(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.getStatement(user.getEmail()));
	}
}
