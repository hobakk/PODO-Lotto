package com.example.sixnumber.user.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.UnifiedResponse;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.lotto.dto.SixNumberResponse;
import com.example.sixnumber.user.dto.CashNicknameResponse;
import com.example.sixnumber.user.dto.ChargingRequest;
import com.example.sixnumber.user.dto.ChargingResponse;
import com.example.sixnumber.user.dto.CookieAndTokenResponse;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.dto.UserResponse;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<UnifiedResponse<?>> signup(@RequestBody SignupRequest request) {
		return ResponseEntity.ok(userService.signUp(request));
	}

	@PostMapping("/signin")
	public ResponseEntity<UnifiedResponse<?>> signin(@RequestBody SigninRequest request, HttpServletResponse response) {
		CookieAndTokenResponse result = userService.signIn(request);
		response.addCookie(result.getAccessCookie());
		response.addHeader(JwtProvider.AUTHORIZATION_HEADER, result.getEnCodedRefreshToken());
		return ResponseEntity.ok(UnifiedResponse.ok("로그인 성공"));
	}

	@PostMapping("/logout")
	public ResponseEntity<UnifiedResponse<?>> logout(@AuthenticationPrincipal User user,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		Cookie access = userService.logout(request, user);
		response.addCookie(access);
		return ResponseEntity.ok(UnifiedResponse.ok("로그아웃 성공"));
	}

	@PatchMapping("/withdraw")
	public ResponseEntity<UnifiedResponse<?>> withdraw(@RequestBody OnlyMsgRequest request,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.withdraw(request, user.getEmail()));
	}

	// 로그인 후 화면에 바로 띄울지에 대한 고민
	@GetMapping("/cash")
	public ResponseEntity<UnifiedResponse<CashNicknameResponse>> getCashNickname(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.getCashNickname(user));
	}

	@GetMapping("/charging")
	public ResponseEntity<UnifiedResponse<List<ChargingResponse>>> getCharges(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.getCharges(user.getId()));
	}

	@PostMapping("/charging")
	public ResponseEntity<UnifiedResponse<?>> charging(@RequestBody ChargingRequest chargingRequest,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.charging(chargingRequest, user));
	}

	@PatchMapping("/paid")
	public ResponseEntity<UnifiedResponse<?>> setPaid(@RequestBody OnlyMsgRequest request,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.setPaid(request, user.getEmail()));
	}

	@GetMapping("/statement")
	public ResponseEntity<UnifiedResponse<List<StatementResponse>>> getStatement(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.getStatement(user.getEmail()));
	}

	@PatchMapping("/update")
	public ResponseEntity<UnifiedResponse<?>> updata(@RequestBody SignupRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.update(request, user));
	}

	@GetMapping("/my-information")
	public ResponseEntity<UnifiedResponse<UserResponse>> getMyInformation(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.getMyInformation(user.getId()));
	}

	@PostMapping("/check-pw")
	public ResponseEntity<UnifiedResponse<?>> checkPW(@RequestBody OnlyMsgRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.checkPW(request, user.getPassword()));
	}

	@GetMapping("/sixnumber-list")
	public ResponseEntity<UnifiedResponse<List<SixNumberResponse>>> getBuySixNumberList(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.getBuySixNumberList(user.getId()));
	}
}
