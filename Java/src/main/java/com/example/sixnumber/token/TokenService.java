package com.example.sixnumber.token;

import javax.servlet.http.Cookie;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.sixnumber.global.dto.TokenRequest;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.user.dto.MyInformationResponse;
import com.example.sixnumber.user.entity.User;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class TokenService {
	private final JwtProvider jwtProvider;
	private final Manager manager;

	public UserIfAndCookieResponse getInformationAfterCheckLogin(TokenRequest request) {
		try {
			if (!jwtProvider.validateToken(request.getAccessToken()))
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다");
		} catch (ExpiredJwtException e) {
			String[] idEmail = jwtProvider.validateRefreshToken(request.getRefreshToken());
			if (idEmail.length != 2) throw new IllegalArgumentException("RefreshToken exception");

			String accessToken = jwtProvider.accessToken(idEmail[1], Long.parseLong(idEmail[0]));
			Cookie cookie = new Cookie("accessToken", accessToken);
			cookie.setPath("/");

			User user = manager.findUser(idEmail[1]);
			MyInformationResponse myInformationResponse = new MyInformationResponse(user);
			return new UserIfAndCookieResponse(myInformationResponse, cookie);
		}

		User user = manager.findUser(jwtProvider.getTokenInUserId(request.getAccessToken()));
		MyInformationResponse myInformationResponse = new MyInformationResponse(user);
		return new UserIfAndCookieResponse(myInformationResponse, null);
	}
}
