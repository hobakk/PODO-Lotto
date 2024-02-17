package com.example.sixnumber.fixture;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.sixnumber.global.dto.UnifiedResponse;

public class TestUtil {
	public static <T> void UnifiedResponseEquals(UnifiedResponse<T> response, int code, String msg) {
		assertEquals(response.getMsg(), msg);
		assertEquals(response.getCode(), code);
	}

	public static <T> void UnifiedResponseEquals(UnifiedResponse<T> response, int code, String msg, Class<T> clazz) {
		assertEquals(response.getMsg(), msg);
		assertEquals(response.getCode(), code);
		if (clazz != null) assertNotNull(response.getData());
	}

	public static <T> void UnifiedResponseListEquals(UnifiedResponse<List<T>> response, int code, String msg) {
		assertEquals(response.getMsg(), msg);
		assertEquals(response.getCode(), code);
		assertNotNull(response.getData());
	}

	public static <T> void UnifiedResponsePageEquals(UnifiedResponse<Page<T>> response, int code, String msg) {
		assertEquals(response.getMsg(), msg);
		assertEquals(response.getCode(), code);
		assertNotNull(response.getData());
	}
}
