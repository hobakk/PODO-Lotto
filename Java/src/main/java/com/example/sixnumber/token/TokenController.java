package com.example.sixnumber.token;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.ApiResponse;
import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.TokenDto;
import com.example.sixnumber.user.dto.MyInformationResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jwt")
public class TokenController {
	private final TokenService tokenService;

	@PostMapping("/check/login")
	public ResponseEntity<ItemApiResponse<MyInformationResponse>> getInformationAfterCheckLogin(
		@RequestBody TokenDto request,
		HttpServletResponse response
	) {
		UserIfAndCookieResponse userIfAndCookieResponse = tokenService.getInformationAfterCheckLogin(request);
		if (userIfAndCookieResponse.getCookie() != null) response.addCookie(userIfAndCookieResponse.getCookie());

		return ResponseEntity.ok(ItemApiResponse.ok("조회 및 재발급 성공", userIfAndCookieResponse.getResponse()));
	}

	@PostMapping("/renew/access")
	public ResponseEntity<ApiResponse> renewAccessToken(@RequestBody String refreshToken, HttpServletResponse response) {
		response.addCookie(tokenService.renewAccessToken(refreshToken));
		return ResponseEntity.ok(ApiResponse.ok("AccessToken 재발급 성공"));
	}

}
