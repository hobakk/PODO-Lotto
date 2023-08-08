package com.example.sixnumber.global.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.sixnumber.global.dto.ItemApiResponse;
import com.example.sixnumber.global.dto.TokenRequest;
import com.example.sixnumber.user.dto.MyInformationResponse;
import com.example.sixnumber.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JwtController {
	private final JwtProvider jwtProvider;
	private final Manager manager;

	@PostMapping("/api/jwt/refresh/check")
	public ResponseEntity<ItemApiResponse<MyInformationResponse>> getInformation(
		@RequestBody TokenRequest request,
		HttpServletResponse response
	) {
		if (!jwtProvider.validateRefreshToken(request.getAccessToken(), request.getRefreshToken()))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다");

		String[] idEmail = jwtProvider.getIdEmail(request.getRefreshToken()).split(",");
		Cookie accessToken = new Cookie("accessToken", jwtProvider
			.accessToken(idEmail[1], Long.parseLong(idEmail[0])));
		accessToken.setPath("/");
		response.addCookie(accessToken);

		User user = manager.findUser(idEmail[1]);
		MyInformationResponse myInformationResponse = new MyInformationResponse(user);
		return ResponseEntity.ok(ItemApiResponse.ok("조회 및 재발급 성공", myInformationResponse));
	}
}
