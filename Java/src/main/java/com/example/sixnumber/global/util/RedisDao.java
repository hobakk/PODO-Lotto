package com.example.sixnumber.global.util;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.example.sixnumber.global.exception.CustomException;
import com.example.sixnumber.global.exception.ErrorCode;

@Component
public class RedisDao {
	private final RedisTemplate<String, String> redisTemplate;
	private final ValueOperations<String, String> values;

	public static final String RT_KEY = "RT: ";
	public static final String CHARGE_KEY = "CHARGE: ";
	public static final String AUTH_KEY = "AUTH: ";

	public RedisDao(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.values = redisTemplate.opsForValue();
	}

	public String getValue(String key) {
		return values.get(key);
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

	public void setValues(String key, String value, Long time, TimeUnit timeUnit) {
		values.set(key, value, time, timeUnit);
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

	public boolean isEqualsBlackList(String key) {
		return values.get(key) != null;
	}

	public void setBlackList(String token, Long remainingTime) {
		if (remainingTime != 0) values.set(token, "Black", remainingTime, TimeUnit.MILLISECONDS);
	}
}
