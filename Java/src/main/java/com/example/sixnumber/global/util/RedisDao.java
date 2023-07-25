package com.example.sixnumber.global.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.example.sixnumber.global.exception.OverlapException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisDao {
	private final RedisTemplate<String, String> redisTemplate;

	public String getValue(String key) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		return values.get(key) == null ? "" : values.get(key);
	}

	public Set<String> getKeysList(String key) {
		return redisTemplate.keys("*" + key + "*");
	}

	public List<String> multiGet(String key) {
		Set<String> keys = getKeysList(key);
		if (keys.size() == 0) throw new IllegalArgumentException("충전 요청이 존재하지 않습니다");

		ValueOperations<String, String> values = redisTemplate.opsForValue();
		return values.multiGet(keys);
	}

	public void setValues(String key, String data) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		values.set(key, data);
	}

	public void setValues(String key, String data, Long time, TimeUnit timeUnit) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		values.set(key, data, time, timeUnit);
	}

	public List<String> getValuesList(String key) {
		ListOperations<String, String> listOperations = redisTemplate.opsForList();
		if (listOperations.range(key, 0, -1).isEmpty())
			throw new IllegalArgumentException("당첨 번호 정보가 존재하지 않습니다");

		return listOperations.range(key, 0, -1);
	}

	public void deleteValues(String key) {
		redisTemplate.delete(key);
	}

	public boolean deleteIfNotNull(String key) {
		if (!getValue(key).isEmpty()) {
			deleteValues(key);
			return true;
		}
		return false;
	}

	public void overlapLogin(String key) {
		boolean isNull = deleteIfNotNull(key);
		if (isNull) throw new OverlapException("Duplicate login");
	}
}
