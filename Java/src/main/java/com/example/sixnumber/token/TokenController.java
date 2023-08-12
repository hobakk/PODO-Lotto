package com.example.sixnumber.token;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.TokenRequest;
import com.example.sixnumber.user.dto.MyInformationResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TokenController {
	private final TokenService tokenService;

	@PostMapping("/api/jwt/refresh/check")
	public ResponseEntity<ItemApiResponse<MyInformationResponse>> getInformationAfterCheckLogin(
		@RequestBody TokenRequest request,
		HttpServletResponse response
	) {
		UserIfAndCookieResponse userIfAndCookieResponse = tokenService.getInformationAfterCheckLogin(request);
		if (userIfAndCookieResponse.getCookie() != null) response.addCookie(userIfAndCookieResponse.getCookie());

		return ResponseEntity.ok(ItemApiResponse.ok("조회 및 재발급 성공", userIfAndCookieResponse.getResponse()));
	}
}
