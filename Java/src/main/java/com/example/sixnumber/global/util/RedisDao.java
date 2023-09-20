package com.example.sixnumber.global.util;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class RedisDao {
	private final JwtProvider jwtProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final ValueOperations<String, String> values;

	public static final String RT_KEY = "RT: ";
	public static final String CHARGE_KEY = "CHARGE: ";
	public static final String AUTH_KEY = "AUTH: ";

	public RedisDao(JwtProvider jwtProvider, RedisTemplate<String, String> redisTemplate) {
		this.jwtProvider = jwtProvider;
		this.redisTemplate = redisTemplate;
		this.values = redisTemplate.opsForValue();
	}

	public String getValue(String key, String refreshTokenPointer) {
		switch (key) {
			case RT_KEY: return values.get(RT_KEY + refreshTokenPointer);
			case CHARGE_KEY: return values.get(CHARGE_KEY + refreshTokenPointer);
			case AUTH_KEY: return values.get(AUTH_KEY + refreshTokenPointer);
			default: throw new CustomException(ErrorCode.INVALID_INPUT);
		}
	}

	public Set<String> getKeysList(Object object) {
		if (object instanceof Long) return redisTemplate.keys("*" + RT_KEY + object + "*");
		else if (object instanceof String) return redisTemplate.keys("*" + object + "*");
		else throw new CustomException(ErrorCode.INVALID_INPUT);
	}

	public Set<String> getChargeList(Long userId) {
		return redisTemplate.keys("*" + CHARGE_KEY + userId + "*");
	}

	public List<String> multiGet(Object object) {
		Set<String> keys;
		if (object instanceof Long) {
			keys = getKeysList(CHARGE_KEY + object);
		} else if (object instanceof String) {
			if (object.equals("All")) keys = getKeysList(CHARGE_KEY);
			else keys = getKeysList(object);
		} else throw new CustomException(ErrorCode.INVALID_INPUT);

		if (keys.size() == 0) throw new IllegalArgumentException("충전 요청이 존재하지 않습니다");

		return values.multiGet(keys);
	}

	public void setRefreshToken(String refreshPointer, String data, Long time, TimeUnit timeUnit) {
		values.set(RT_KEY + refreshPointer, data, time, timeUnit);
	}

	public void setValues(String key, String data, Long time, TimeUnit timeUnit) {
		values.set(CHARGE_KEY + key, data, time, timeUnit);
	}

	public void delete(String value, String subject) {
		if (subject.equals(JwtProvider.REFRESH_TOKEN)) redisTemplate.delete(RT_KEY + value);
		else redisTemplate.delete(CHARGE_KEY + value);
	}

	public boolean isEqualsBlackList(String key) {
		return values.get(key) != null;
	}

	public void setBlackList(String token) {
		try {
			if (jwtProvider.validateToken(token)) {
				ValueOperations<String, String> values = redisTemplate.opsForValue();
				Long remainingTime = jwtProvider.getRemainingTime(token);
				if (remainingTime != 0) {
					values.set(token, "Black", jwtProvider.getRemainingTime(token), TimeUnit.MILLISECONDS);
				}
			} else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 accessToken 입니다");
		} catch (ExpiredJwtException e) {
			return;
		}
	}
}
