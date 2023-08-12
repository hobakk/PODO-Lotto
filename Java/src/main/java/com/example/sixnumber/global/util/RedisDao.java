package com.example.sixnumber.global.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;
import com.example.sixnumber.global.exception.OverlapException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisDao {
	private final RedisTemplate<String, String> redisTemplate;
	private final String RTK = "RT: ";
	private final String STMT = "STMT: ";

	public String getValue(Long userId) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		String inRedisValue = values.get(RTK + userId);
		return inRedisValue == null ? "" : inRedisValue;
	}

	public boolean checkKey(Long userId) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		return values.get(RTK + userId) != null;
	}

	public Set<String> getKeysList(Object object) {
		if (object instanceof Long userId) return redisTemplate.keys("*" + RTK + userId + "*");
		else if (object instanceof String key) return redisTemplate.keys("*" + key + "*");
		else throw new CustomException(ErrorCode.INVALID_INPUT);
	}

	public List<String> multiGet(Object object) {
		Set<String> keys = new HashSet<>();
		if (object instanceof Long userId) {
			keys = getKeysList(STMT + userId);
		} else if (object instanceof String key) {
			if (key.equals("All")) keys = getKeysList(STMT);
		} else throw new CustomException(ErrorCode.INVALID_INPUT);

		if (keys.size() == 0) throw new IllegalArgumentException("충전 요청이 존재하지 않습니다");

		ValueOperations<String, String> values = redisTemplate.opsForValue();
		return values.multiGet(keys);
	}

	public void setValues(Long userId, String data) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		values.set(RTK + userId, data);
	}

	public void setValues(String key, String data, Long time, TimeUnit timeUnit) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		values.set(STMT + key, data, time, timeUnit);
	}

	public List<String> getValuesList(String key) {
		ListOperations<String, String> listOperations = redisTemplate.opsForList();
		if (listOperations.range(key, 0, -1).isEmpty())
			throw new IllegalArgumentException("당첨 번호 정보가 존재하지 않습니다");

		return listOperations.range(key, 0, -1);
	}

	public void deleteValues(Object object) {
		if (object instanceof Long userId) redisTemplate.delete(RTK + userId);
		else if (object instanceof String key) redisTemplate.delete(STMT + key);
		else throw new CustomException(ErrorCode.INVALID_INPUT);
	}

	public boolean deleteIfNotNull(Long userId) {
		if (!getValue(userId).equals("")) {
			deleteValues(userId);
			return true;
		}
		return false;
	}

	public void overlapLogin(Long userId) {
		boolean isNull = deleteIfNotNull(userId);
		if (isNull) throw new OverlapException("중복된 로그인입니다");
	}
}
