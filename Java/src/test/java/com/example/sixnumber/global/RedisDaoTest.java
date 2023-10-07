package com.example.sixnumber.global;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.example.sixnumber.global.util.RedisDao;

@ExtendWith(MockitoExtension.class)
public class RedisDaoTest {
	@InjectMocks
	private RedisDao redisDao;

	@Mock
	private RedisTemplate<String, String> redisTemplate;
	@Mock
	private ValueOperations<String, String> valueOperations;

	String key;

	@BeforeEach
	public void setup() {
		key = "some_key";
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

}
