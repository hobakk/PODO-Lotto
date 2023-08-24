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

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisDao {
	private final JwtProvider jwtProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final String RTK = "RT: ";
	private final String STMT = "STMT: ";

	public String getValue(String refreshToken) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		return values.get(RTK + refreshToken);
	}

	public Set<String> getKeysList(Object object) {
		if (object instanceof Long userId) return redisTemplate.keys("*" + RTK + userId + "*");
		else if (object instanceof String key) return redisTemplate.keys("*" + key + "*");
		else throw new CustomException(ErrorCode.INVALID_INPUT);
	}

	public List<String> multiGet(Object object) {
		Set<String> keys;
		if (object instanceof Long userId) {
			keys = getKeysList(STMT + userId);
		} else if (object instanceof String key) {
			if (key.equals("All")) keys = getKeysList(STMT);
			else keys = getKeysList(key);
		} else throw new CustomException(ErrorCode.INVALID_INPUT);

		if (keys.size() == 0) throw new IllegalArgumentException("충전 요청이 존재하지 않습니다");

		ValueOperations<String, String> values = redisTemplate.opsForValue();
		return values.multiGet(keys);
	}

	public void setRefreshToken(String refreshPointer, String data, Long time, TimeUnit timeUnit) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		values.set(RTK + refreshPointer, data, time, timeUnit);
	}

	public void setValues(String key, String data, Long time, TimeUnit timeUnit) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		values.set(STMT + key, data, time, timeUnit);
	}

	// public List<String> getValuesList(String key) {
	// 	ListOperations<String, String> listOperations = redisTemplate.opsForList();
	// 	if (listOperations.range(key, 0, -1).isEmpty())
	// 		throw new IllegalArgumentException("당첨 번호 정보가 존재하지 않습니다");
	//
	// 	return listOperations.range(key, 0, -1);
	// }

	public void deleteValues(Object object) {
		if (object instanceof Long userId) redisTemplate.delete(RTK + userId);
		else if (object instanceof String key) redisTemplate.delete(STMT + key);
		else throw new CustomException(ErrorCode.INVALID_INPUT);
	}

	public void deleteInRedisValueIsNotNull(String refreshPointer) {
		String refreshToken = getValue(refreshPointer);
		if (refreshToken != null) redisTemplate.delete(refreshToken);
	}

	public boolean isEqualsBlackList(String key) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		return values.get(key) != null;
	}

	public void setBlackList(String token) {
		if (jwtProvider.validateToken(token)) {
			ValueOperations<String, String> values = redisTemplate.opsForValue();
			Long remainingTime = jwtProvider.getRemainingTime(token);
			if (remainingTime != 0) {
				values.set(token, "Black", jwtProvider.getRemainingTime(token), TimeUnit.MILLISECONDS);
			}

		} else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 accessToken 입니다");
	}
}
