package com.example.sixnumber.token;

import javax.servlet.http.Cookie;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.dto.TokenRequest;
import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
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
		if (jwtProvider.validateToken(request.getAccessToken())) {
			User user = manager.findUser(jwtProvider.getTokenInUserId(request.getAccessToken()));
			MyInformationResponse myInformationResponse = new MyInformationResponse(user);
			return new UserIfAndCookieResponse(myInformationResponse, null);
		} else {
			try {
				Cookie cookie = createCookie(request.getRefreshToken());
				User user = manager.findUser(jwtProvider.getTokenInUserId(request.getRefreshToken()));
				MyInformationResponse myInformationResponse = new MyInformationResponse(user);
				return new UserIfAndCookieResponse(myInformationResponse, cookie);
			} catch (ExpiredJwtException e) {
				throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
			}
		}
	}

	public Cookie renewAccessToken(String refreshToken) {
		return createCookie(refreshToken);
	}

	private Cookie createCookie(String refreshToken) {
		String[] idEmail = jwtProvider.validateRefreshToken(refreshToken);
		if (idEmail != null && idEmail.length != 2) throw new IllegalArgumentException("RefreshToken exception");

		String accessToken = jwtProvider.accessToken(idEmail[1], Long.parseLong(idEmail[0]));
		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setPath("/");
		return cookie;
	}
}
