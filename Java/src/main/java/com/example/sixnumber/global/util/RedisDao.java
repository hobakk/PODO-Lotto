package com.example.sixnumber.global.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
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

	public Set<String> getKeysList(String key) {
		ScanOptions options = ScanOptions.scanOptions().match(key + "*").count(100).build();
		return redisTemplate.execute((RedisCallback<? extends Set<String>>) connection -> {
			Set<String> keys = new HashSet<>();
			Cursor<byte[]> cursor = connection.scan(options);
			while (cursor.hasNext()) {
				keys.add(new String(cursor.next()));
			}
			cursor.close();
			return keys;
		});
	}

	public List<String> multiGet(String key) {
		Set<String> keys = getKeysList(key);
		if (keys.size() == 0) throw new CustomException(ErrorCode.NOT_FOUND);

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
