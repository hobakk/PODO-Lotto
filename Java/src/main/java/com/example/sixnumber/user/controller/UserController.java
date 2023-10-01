package com.example.sixnumber.user.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.example.sixnumber.user.dto.EmailAuthCodeRequest;
import com.example.sixnumber.user.dto.EmailRequest;
import com.example.sixnumber.user.dto.FindPasswordRequest;
import com.example.sixnumber.user.dto.OnlyMsgRequest;
import com.example.sixnumber.user.dto.SigninRequest;
import com.example.sixnumber.user.dto.SignupRequest;
import com.example.sixnumber.user.dto.StatementModifyMsgRequest;
import com.example.sixnumber.user.dto.StatementResponse;
import com.example.sixnumber.user.dto.UserResponse;
import com.example.sixnumber.user.dto.UserResponseAndEncodedRefreshDto;
import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/email")
	public ResponseEntity<UnifiedResponse<?>> sendAuthCodeToEmail(
		@Valid @RequestBody EmailRequest request,
		Errors errors
	) {
		return ResponseEntity.ok(userService.sendAuthCodeToEmail(request, errors));
	}

	@PostMapping("/email/auth-code")
	public ResponseEntity<UnifiedResponse<?>> compareAuthCode(@RequestBody EmailAuthCodeRequest request) {
		return ResponseEntity.ok(userService.compareAuthCode(request));
	}

	@PostMapping("/signup")
	public ResponseEntity<UnifiedResponse<?>> signUp(
		@Valid @RequestBody SignupRequest request,
		Errors errors
	) {
		return ResponseEntity.ok(userService.signUp(request, errors));
	}

	@PostMapping("/signin")
	public ResponseEntity<UnifiedResponse<?>> signIn(
		@RequestBody SigninRequest request,
		HttpServletResponse response,
		Errors errors
	) {
		UnifiedResponse<?> unifiedResponse = userService.signIn(response, request, errors);
		if (unifiedResponse.getCode() == HttpStatus.OK.value()) return ResponseEntity.ok(unifiedResponse);
		else return ResponseEntity.badRequest().body(unifiedResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<UnifiedResponse<?>> logout(
		@AuthenticationPrincipal User user,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		Cookie access = userService.logout(request, user);
		response.addCookie(access);
		return ResponseEntity.ok(UnifiedResponse.ok("로그아웃 성공"));
	}

	@PatchMapping("/withdraw")
	public ResponseEntity<UnifiedResponse<?>> withdraw(
		@RequestBody OnlyMsgRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(userService.withdraw(request, user.getEmail()));
	}

	// 로그인 후 화면에 바로 띄울지에 대한 고민
	@GetMapping("/cash")
	public ResponseEntity<UnifiedResponse<CashNicknameResponse>> getCashAndNickname(
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(userService.getCashAndNickname(user));
	}

	@PostMapping("/charge")
	public ResponseEntity<UnifiedResponse<?>> charging(
		@RequestBody ChargingRequest chargingRequest,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(userService.charging(chargingRequest, user));
	}

	@GetMapping("/charge")
	public ResponseEntity<UnifiedResponse<ChargingResponse>> getCharge(
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(userService.getCharge(user.getId()));
	}

	@DeleteMapping("/charge/{key}")
	public ResponseEntity<UnifiedResponse<?>> deleteCharge(
		@PathVariable() String key,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(userService.deleteCharge(key, user));
	}

	@PatchMapping("/premium")
	public ResponseEntity<UnifiedResponse<?>> setPremium(
		@RequestBody OnlyMsgRequest request,
		@AuthenticationPrincipal User user
	) {
		if (request.getMsg().equals("월정액 해지")) return ResponseEntity.ok(userService.changeToUser(user));
		else return ResponseEntity.ok(userService.changeToPaid(user.getEmail()));
	}

	@GetMapping("/statement")
	public ResponseEntity<UnifiedResponse<List<StatementResponse>>> getStatement(
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(userService.getStatement(user.getEmail()));
	}

	@PatchMapping("/statement")
	public ResponseEntity<UnifiedResponse<?>> modifyStatementMsg(
		@RequestBody StatementModifyMsgRequest request
	) {
		return ResponseEntity.ok(userService.modifyStatementMsg(request));
	}

	@PatchMapping("/update")
	public ResponseEntity<UnifiedResponse<?>> update(
		@RequestBody SignupRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(userService.update(request, user));
	}

	@GetMapping("/my-information")
	public ResponseEntity<UnifiedResponse<UserResponse>> getMyInformation(
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(userService.getMyInformation(user.getId()));
	}

	@PostMapping("/check-pw")
	public ResponseEntity<UnifiedResponse<?>> comparePassword(
		@RequestBody OnlyMsgRequest request,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(userService.comparePassword(request, user.getPassword()));
	}

	@GetMapping("/sixnumber-list")
	public ResponseEntity<UnifiedResponse<List<SixNumberResponse>>> getBuySixNumberList(
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(userService.getBuySixNumberList(user.getId()));
	}

	@GetMapping("/oauth2/my-information")
	public ResponseEntity<UnifiedResponse<UserResponse>> oauth2LoginAfterGetUserIfAndRefreshToken(
		@AuthenticationPrincipal User user,
		HttpServletResponse response
	) {
		UserResponseAndEncodedRefreshDto dto = userService.oauth2LoginAfterGetUserIfAndRefreshToken(user.getId());
		response.addHeader(JwtProvider.AUTHORIZATION_HEADER, "Bearer " + dto.getEncodedRefreshToken());
		return ResponseEntity.ok(UnifiedResponse.ok("조회 성공", dto.getUserResponse()));
	}

	@PostMapping("/find-password")
	public ResponseEntity<UnifiedResponse<?>> findPassword(
		@RequestBody FindPasswordRequest request,
		Errors errors
	) {
		return ResponseEntity.ok(userService.findPassword(request, errors));
	}
}
