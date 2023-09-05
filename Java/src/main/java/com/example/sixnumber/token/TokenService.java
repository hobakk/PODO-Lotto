package com.example.sixnumber.token;

import javax.servlet.http.Cookie;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.global.util.JwtProvider;
import com.example.sixnumber.global.util.Manager;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.user.entity.User;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class TokenService {
	private final PasswordEncoder encoder;
	private final JwtProvider jwtProvider;
	private final RedisDao redisDao;
	private final Manager manager;

	public Cookie reIssuance(ReIssuanceRequest request) {
		User target = manager.findUser(request.getEmail());
		if (!target.getId().equals(request.getUserId())) throw new CustomException(ErrorCode.INVALID_INPUT);

		String pointer = target.getRefreshPointer();
		String refreshTokenInRedis = redisDao.getValue(pointer);
		if (!encoder.matches(refreshTokenInRedis, request.getRefreshToken())) {
			throw new CustomException(ErrorCode.NO_MATCHING_INFO_FOUND);
		}

		if (!jwtProvider.validateRefreshToken(refreshTokenInRedis)) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		return createCookie(refreshTokenInRedis, pointer);
	}

	// public Cookie renewAccessToken(String refreshToken) {
	// 	return createCookie(refreshToken);
	// }

	private Cookie createCookie(String refreshToken, String pointer) {
		if (!jwtProvider.validateRefreshToken(refreshToken)) throw new CustomException(ErrorCode.INVALID_TOKEN);

		String accessToken = jwtProvider.accessToken(pointer);
		Long remainingSeconds = Math.max(jwtProvider.getRemainingTime(refreshToken) /1000, 0);
		return jwtProvider.createCookie(JwtProvider.ACCESS_TOKEN, accessToken, remainingSeconds);
	}
}
