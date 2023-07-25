package com.example.sixnumber.global;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.example.sixnumber.fixture.TestDataFactory;
import com.example.sixnumber.global.exception.OverlapException;
import com.example.sixnumber.global.util.RedisDao;
import com.example.sixnumber.user.dto.WinNumberRequest;

@ExtendWith(MockitoExtension.class)
public class RedisDaoTest {
	@InjectMocks
	private RedisDao redisDao;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	private ValueOperations<String, String> valueOperations;
	private ListOperations<String, String> listOperations;
	String key = "";

	@BeforeEach
	public void setup() {
		key = "some_key";
		valueOperations = mock(ValueOperations.class);
		listOperations = mock(ListOperations.class);
	}

	@Test
	void multiGet() {
		Set<String> value = mock(Set.class);
		when(value.size()).thenReturn(0);

		when(redisTemplate.keys(anyString())).thenReturn(value);

		Assertions.assertThrows(IllegalArgumentException.class, ()->redisDao.multiGet(anyString()));

		verify(redisTemplate).keys(anyString());
	}

	@Test
	void getValuesList() {
		when(redisTemplate.opsForList()).thenReturn(listOperations);
		when(listOperations.range(anyString(), anyLong(), anyLong())).thenReturn(new ArrayList<>());

		Assertions.assertThrows(IllegalArgumentException.class, ()->redisDao.getValuesList(key));

		verify(listOperations).range(anyString(), anyLong(), anyLong());
	}

	@Test
	void overlapLogin() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(key)).thenReturn("value");

		Assertions.assertThrows(OverlapException.class, () -> redisDao.overlapLogin(key));

		verify(valueOperations, times(2)).get(key);
	}

	@Test
	void setWinNumber_success_manySize() {
		WinNumberRequest request = TestDataFactory.winNumberRequest();
		String result = request.getTime()+","+request.getDate()+","+request.getPrize()+","
			+request.getWinner()+","+request.getNumbers();
		ListOperations<String, String> listOperations = mock(ListOperations.class);

		when(redisTemplate.opsForList()).thenReturn(listOperations);
		when(listOperations.size("WNL")).thenReturn(5L);
		when(listOperations.leftPop("WNL")).thenReturn("previousValue");
		when(listOperations.rightPush("WNL", result)).thenReturn(1L);

		Long size = redisDao.setWinNumber("WNL", request);

		verify(listOperations, times(2)).size("WNL");
		verify(listOperations).leftPop(anyString());
		verify(listOperations).rightPush("WNL", result);
		assertEquals(listOperations.size("WNL"), 5L);
		assertEquals(size, 5L);
	}
}
